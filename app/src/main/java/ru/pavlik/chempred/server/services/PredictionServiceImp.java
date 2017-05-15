package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.util.TransferFunctionType;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.server.model.Compound;
import ru.pavlik.chempred.server.model.Descriptor;
import ru.pavlik.chempred.server.model.NeuralNetworkModel;
import ru.pavlik.chempred.server.utils.DescriptorUtils;
import ru.pavlik.chempred.server.utils.HibernateUtil;
import ru.pavlik.chempred.server.utils.SmilesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class PredictionServiceImp extends RemoteServiceServlet implements PredictionService {

    private static final int LEARN_ITERATION = 10_000;
    private static final double LEARN_MAX_ERROR = 0.0001;
    private static final double LEARN_RATE = 0.1;

    public double train() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Compound");
        List<Compound> compounds = query.list();

        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        int inputSize = 6;
        NeuralNetwork neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputSize, inputSize / 2, 1);
        ((LMS) neuralNetwork.getLearningRule()).setMaxError(LEARN_MAX_ERROR);
        ((LMS) neuralNetwork.getLearningRule()).setLearningRate(LEARN_RATE);
        ((LMS) neuralNetwork.getLearningRule()).setMaxIterations(LEARN_ITERATION);

        double[][] inputs = new double[compounds.size()][inputSize];
        double[] outputs = new double[compounds.size()];
        DataSet dataSet = new DataSet(inputSize, 1);

        BCUTDescriptor bcutDescriptor = new BCUTDescriptor();
        for (int i = 0; i < compounds.size(); i++) {
            Compound compound = compounds.get(i);
            IAtomContainer atomContainer;
            try {
                atomContainer = smilesParser.parseSmiles(compound.getSmiles());
            } catch (InvalidSmilesException e) {
                log.error("Error occurred while parse smile: ", e);
                continue;
            }
            DescriptorValue calculate = bcutDescriptor.calculate(atomContainer);
            DoubleArrayResult arrayResult = (DoubleArrayResult) calculate.getValue();
            double[] arrayInputs = new double[arrayResult.length()];
            for (int j = 0; j < arrayResult.length(); j++) {
                arrayInputs[j] = arrayResult.get(j);
            }
            inputs[i] = arrayInputs;
            outputs[i] = compound.getExperimentalFactor();
        }

        inputs = normalizeData(inputs);

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (double v : outputs) {
            if (v > max) {
                max = v;
            }
            if (v < min) {
                min = v;
            }
        }

        outputs = normalizeData(outputs, min, max);

        for (int i = 0; i < inputs.length; i++) {
            dataSet.addRow(inputs[i], new double[]{outputs[i]});
        }
        neuralNetwork.learn(dataSet);

        NeuralNetworkModel networkModel = new NeuralNetworkModel();
        networkModel.setNeuralNetwork(neuralNetwork);
        networkModel.setMinOutputValue(min);
        networkModel.setMaxOutputValue(max);
        session.saveOrUpdate(networkModel);
        session.getTransaction().commit();

        return ((LMS) neuralNetwork.getLearningRule()).getTotalNetworkError();
    }

    @Override
    public double train(List<CompoundDao> compounds) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Descriptor");

        List<Descriptor> sourceDescriptors = query.list();

        double[][] inputs = new double[compounds.size()][sourceDescriptors.size()];
        double[] outputs = new double[compounds.size()];

        for (int i = 0; i < compounds.size(); i++) {
            CompoundDao compound = compounds.get(i);
            Map<String, Integer> countDescriptors = new TreeMap<>();
            List<String> descriptors = DescriptorUtils.getDescriptors(compound);

            for (Descriptor sourceDescriptor : sourceDescriptors) {
                countDescriptors.put(sourceDescriptor.getName(), 0);
            }

            for (String descriptor : descriptors) {
                if (countDescriptors.containsKey(descriptor)) {
                    countDescriptors.put(descriptor, countDescriptors.get(descriptor) + 1);
                }
            }

            List<Integer> values = new ArrayList<>(countDescriptors.values());
            for (int j = 0; j < values.size(); j++) {
                inputs[i][j] = values.get(j);
            }

            outputs[i] = compound.getLowFactor();
        }

        inputs = normalizeData(inputs);

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (double v : outputs) {
            if (v > max) {
                max = v;
            }
            if (v < min) {
                min = v;
            }
        }

        outputs = normalizeData(outputs, min, max);

        DataSet dataSet = new DataSet(sourceDescriptors.size(), 1);
        NeuralNetwork neuralNetwork = getNeuralNetwork(sourceDescriptors.size());

        for (int i = 0; i < inputs.length; i++) {
            dataSet.addRow(inputs[i], new double[]{outputs[i]});
        }
        neuralNetwork.learn(dataSet);

        NeuralNetworkModel networkModel = new NeuralNetworkModel();
        networkModel.setNeuralNetwork(neuralNetwork);
        networkModel.setMinOutputValue(min);
        networkModel.setMaxOutputValue(max);
        session.saveOrUpdate(networkModel);
        session.getTransaction().commit();

        return ((LMS) neuralNetwork.getLearningRule()).getTotalNetworkError();
    }

    @Override
    public double predict(List<LinkDao> links) {
        String smiles = SmilesUtils.parseStructure(links);
        return predict(smiles);
    }

    @Override
    public double predict(String smiles) {
//        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
//        BCUTDescriptor bcutDescriptor = new BCUTDescriptor();
//        IAtomContainer atomContainer;
//        try {
//            atomContainer = smilesParser.parseSmiles(smiles);
//        } catch (InvalidSmilesException e) {
//            log.error("Error occurred while parse smile: ", e);
//            return -1;
//        }
//        DescriptorValue calculate = bcutDescriptor.calculate(atomContainer);
//        DoubleArrayResult arrayResult = (DoubleArrayResult) calculate.getValue();
//        double[] arrayInputs = new double[arrayResult.length()];
//        for (int j = 0; j < arrayResult.length(); j++) {
//            arrayInputs[j] = arrayResult.get(j);
//        }

        CompoundDao compound = new CompoundDao();
        compound.setSmiles(smiles);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query descriptorsQuery = session.createQuery("from Descriptor");

        List<Descriptor> sourceDescriptors = descriptorsQuery.list();
        double[] inputs = new double[sourceDescriptors.size()];

        Map<String, Integer> countDescriptors = new TreeMap<>();
        List<String> descriptors = DescriptorUtils.getDescriptors(compound);

        for (Descriptor sourceDescriptor : sourceDescriptors) {
            countDescriptors.put(sourceDescriptor.getName(), 0);
        }

        for (String descriptor : descriptors) {
            if (countDescriptors.containsKey(descriptor)) {
                countDescriptors.put(descriptor, countDescriptors.get(descriptor) + 1);
            }
        }

        List<Integer> values = new ArrayList<>(countDescriptors.values());
        for (int j = 0; j < values.size(); j++) {
            inputs[j] = values.get(j);
        }

        inputs = normalizeData(inputs);

        Query neuralNetworkQuery = session.createQuery("from NeuralNetworkModel");
        NeuralNetworkModel networkModel = (NeuralNetworkModel) neuralNetworkQuery.list().get(0);
        session.getTransaction().commit();

        networkModel.getNeuralNetwork().setInput(inputs);
        networkModel.getNeuralNetwork().calculate();
        double[] outputs = networkModel.getNeuralNetwork().getOutput();
        outputs = unnormalizeData(outputs, networkModel.getMinOutputValue(), networkModel.getMaxOutputValue());

        return outputs[0];
    }

    private double[][] normalizeData(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = normalizeData(data[i]);
        }
        return data;
    }

    private double[] normalizeData(double[] data) {
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (double d : data) {
            if (d > maxValue) {
                maxValue = d;
            }
            if (d < minValue) {
                minValue = d;
            }
        }

        return normalizeData(data, minValue, maxValue);
    }

    private double[] normalizeData(double[] data, double min, double max) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (data[i] - min) / max;
        }
        return data;
    }

    private double[] unnormalizeData(double[] data, double min, double max) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] * max + min;
        }

        return data;
    }

    private NeuralNetwork getNeuralNetwork(int inputSize) {
        NeuralNetwork neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputSize, inputSize / 2, 1);
        ((LMS) neuralNetwork.getLearningRule()).setMaxError(LEARN_MAX_ERROR);
        ((LMS) neuralNetwork.getLearningRule()).setLearningRate(LEARN_RATE);
        ((LMS) neuralNetwork.getLearningRule()).setMaxIterations(LEARN_ITERATION);
        return neuralNetwork;
    }
}
