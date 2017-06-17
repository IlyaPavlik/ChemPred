package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.util.TransferFunctionType;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.server.model.Descriptor;
import ru.pavlik.chempred.server.model.NeuralNetworkModel;
import ru.pavlik.chempred.server.utils.DescriptorUtils;
import ru.pavlik.chempred.server.utils.HibernateUtil;
import ru.pavlik.chempred.server.utils.SmilesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
@Slf4j
public class PredictionServiceImp extends RemoteServiceServlet implements PredictionService {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    private static final int LEARN_ITERATION = 10_000;
    private static final double LEARN_MAX_ERROR = 0.0001;
    private static final double LEARN_RATE = 0.1;

    @Override
    public double trainLELValue(final List<CompoundDao> compounds) {
        return train(compounds, true);
    }

    @Override
    public double trainUELValue(final List<CompoundDao> compounds) {
        return train(compounds, false);
    }

    @Override
    public NeuralNetworkParamDao loadNeuralNetworkParams() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query neuralNetworkQuery = session.createQuery("from NeuralNetworkModel");
        NeuralNetworkModel networkModel = (NeuralNetworkModel) neuralNetworkQuery.list().get(0);
        session.getTransaction().commit();
        NeuralNetwork neuralNetwork = networkModel.getNeuralNetwork();
        neuralNetwork.calculate();

        NeuralNetworkParamDao neuralNetworkParam = new NeuralNetworkParamDao();
        neuralNetworkParam.setActivationFunction(neuralNetwork.getLabel());
        neuralNetworkParam.setInputSize(neuralNetwork.getInputsCount());
        neuralNetworkParam.setOutputSize(neuralNetwork.getOutputsCount());
        neuralNetworkParam.setIterations(LEARN_ITERATION);
        neuralNetworkParam.setMaxError(LEARN_MAX_ERROR);
        neuralNetworkParam.setRate(LEARN_RATE);
        neuralNetworkParam.setTotalError(networkModel.getTotalError());

        return neuralNetworkParam;
    }

    @Override
    public double predictLEL(final StructureDao structure) {
        final String smiles = SmilesUtils.parseStructure(structure);
        return predictLEL(smiles);
    }

    @Override
    public double predictUEL(final StructureDao structure) {
        final String smiles = SmilesUtils.parseStructure(structure);
        return predictUEL(smiles);
    }

    @Override
    public double predictLEL(String smiles) {
        return predict(smiles, true);
    }

    @Override
    public double predictUEL(String smiles) {
        return predict(smiles, false);
    }

    @Override
    public List<CompoundDao> predictCompounds(List<CompoundDao> compounds) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query neuralNetworkQuery = session.createQuery("from NeuralNetworkModel");
        NeuralNetworkModel networkModel = (NeuralNetworkModel) neuralNetworkQuery.list().get(0);
        session.getTransaction().commit();

        for (CompoundDao compound : compounds) {
            double[] inputs = buildInput(compound);
            networkModel.getNeuralNetwork().setInput(inputs);
            networkModel.getNeuralNetwork().calculate();
            double[] outputs = networkModel.getNeuralNetwork().getOutput();
            outputs = unnormalizeData(outputs, MIN_VALUE, MAX_VALUE);
            compound.setLowFactorPrediction(outputs[0]);
        }
        return compounds;
    }

    private double[] buildInput(CompoundDao compound) {
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
        session.getTransaction().commit();
        return normalizeData(inputs);
    }

    private double train(final List<CompoundDao> compounds, final boolean useLEL) {
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

            outputs[i] = useLEL ? compound.getLowFactor() : compound.getUpperFactor();
        }

        inputs = normalizeData(inputs);
        outputs = normalizeData(outputs, MIN_VALUE, MAX_VALUE);

        DataSet dataSet = new DataSet(sourceDescriptors.size(), 1);
        NeuralNetwork neuralNetwork = getNeuralNetwork(sourceDescriptors.size());

        for (int i = 0; i < inputs.length; i++) {
            dataSet.addRow(inputs[i], new double[]{outputs[i]});
        }
        neuralNetwork.learn(dataSet);

        final LMS learningRule = ((LMS) neuralNetwork.getLearningRule());
        double totalError = learningRule.getTotalNetworkError();
        NeuralNetworkModel networkModel = new NeuralNetworkModel(useLEL
                ? NeuralNetworkModel.TypeId.LEL
                : NeuralNetworkModel.TypeId.UEL);
        networkModel.setNeuralNetwork(neuralNetwork);
        networkModel.setTotalError(totalError);
        networkModel.setCurrentIteration(learningRule.getCurrentIteration());
        session.saveOrUpdate(networkModel);
        session.getTransaction().commit();

        return totalError;
    }

    private double predict(final String smiles, final boolean useLEL) {
        CompoundDao compound = new CompoundDao();
        compound.setSmiles(smiles);
        double[] inputs = buildInput(compound);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query neuralNetworkQuery = session.createQuery("from NeuralNetworkModel where id = :id");
        neuralNetworkQuery.setParameter("id", useLEL
                ? NeuralNetworkModel.TypeId.LEL.ordinal()
                : NeuralNetworkModel.TypeId.UEL.ordinal());
        NeuralNetworkModel networkModel = (NeuralNetworkModel) neuralNetworkQuery.list().get(0);
        session.getTransaction().commit();

        networkModel.getNeuralNetwork().setInput(inputs);
        networkModel.getNeuralNetwork().calculate();
        double[] outputs = networkModel.getNeuralNetwork().getOutput();
        outputs = unnormalizeData(outputs, MIN_VALUE, MAX_VALUE);

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
        neuralNetwork.setLearningRule(new BackPropagation());
        ((LMS) neuralNetwork.getLearningRule()).setMaxError(LEARN_MAX_ERROR);
        ((LMS) neuralNetwork.getLearningRule()).setLearningRate(LEARN_RATE);
        ((LMS) neuralNetwork.getLearningRule()).setMaxIterations(LEARN_ITERATION);
        neuralNetwork.setLabel(TransferFunctionType.SIGMOID.getTypeLabel());
        return neuralNetwork;
    }

}
