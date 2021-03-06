package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.neuroph.util.TransferFunctionType;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.model.dao.TrainMethod;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.server.model.Compound;
import ru.pavlik.chempred.server.model.Descriptor;
import ru.pavlik.chempred.server.model.NeuralNetworkModel;
import ru.pavlik.chempred.server.model.converter.CompoundConverter;
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

    private static final int LEARN_ITERATION = 10_0000;
    private static final double LEARN_MAX_ERROR = 0.0001;
    private static final double LEARN_RATE = 0.1;

    @Override
    public double trainLELValue(final List<CompoundDao> compounds, final TrainMethod trainMethod) {
        return train(compounds, true, trainMethod);
    }

    @Override
    public double trainUELValue(final List<CompoundDao> compounds, final TrainMethod trainMethod) {
        return train(compounds, false, trainMethod);
    }

    @Override
    public NeuralNetworkParamDao loadNeuralNetworkParams(final boolean useLEL) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query neuralNetworkQuery = session.createQuery("from NeuralNetworkModel where id = :id");
        neuralNetworkQuery.setParameter("id", useLEL
                ? NeuralNetworkModel.TypeId.LEL.ordinal()
                : NeuralNetworkModel.TypeId.UEL.ordinal());
        NeuralNetworkModel networkModel = (NeuralNetworkModel) neuralNetworkQuery.list().get(0);
        session.getTransaction().commit();

        NeuralNetwork neuralNetwork = networkModel.getNeuralNetwork();
        neuralNetwork.calculate();

        NeuralNetworkParamDao neuralNetworkParam = new NeuralNetworkParamDao();
        neuralNetworkParam.setActivationFunction(neuralNetwork.getLabel());
        neuralNetworkParam.setInputSize(neuralNetwork.getInputsCount());
        neuralNetworkParam.setOutputSize(neuralNetwork.getOutputsCount());
        neuralNetworkParam.setTotalIterations(LEARN_ITERATION);
        neuralNetworkParam.setMaxError(LEARN_MAX_ERROR);
        neuralNetworkParam.setRate(LEARN_RATE);
        neuralNetworkParam.setCurrentIterations(networkModel.getCurrentIteration());
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
    public List<CompoundDao> predictAllCompounds(boolean useLEL) {
        final CompoundConverter converter = new CompoundConverter();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query compoundsQuery = session.createQuery("from Compound");
        List<Compound> compounds = compoundsQuery.list();
        List<CompoundDao> compoundDaoList = new ArrayList<>();
        compounds.forEach(compound -> compoundDaoList.add(converter.convertToDao(compound)));

        Query neuralNetworkQuery = session.createQuery("from NeuralNetworkModel where id = :id");
        neuralNetworkQuery.setParameter("id", useLEL
                ? NeuralNetworkModel.TypeId.LEL.ordinal()
                : NeuralNetworkModel.TypeId.UEL.ordinal());
        NeuralNetworkModel networkModel = (NeuralNetworkModel) neuralNetworkQuery.list().get(0);
        session.getTransaction().commit();

        for (CompoundDao compound : compoundDaoList) {
            double[] inputs = buildInput(compound);
            networkModel.getNeuralNetwork().setInput(inputs);
            networkModel.getNeuralNetwork().calculate();
            double[] outputs = networkModel.getNeuralNetwork().getOutput();
            outputs = unnormalizeData(outputs, networkModel.getMinOutput(), networkModel.getMaxOutput());
            if (useLEL) {
                compound.setLowFactorPrediction(outputs[0]);
            } else {
                compound.setUpperFactorPrediction(outputs[0]);
            }
        }
        return compoundDaoList;
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

    private double train(final List<CompoundDao> compounds, final boolean useLEL, final TrainMethod trainMethod) {
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

        inputs = normalizeData(inputs);
        outputs = normalizeData(outputs, min, max);

        DataSet dataSet = new DataSet(sourceDescriptors.size(), 1);
        NeuralNetwork neuralNetwork = getNeuralNetwork(sourceDescriptors.size(), trainMethod);

        for (int i = 0; i < inputs.length; i++) {
            dataSet.addRow(inputs[i], new double[]{outputs[i]});
        }
        neuralNetwork.learn(dataSet);
        NeuralNetworkModel networkModel = new NeuralNetworkModel(useLEL
                ? NeuralNetworkModel.TypeId.LEL
                : NeuralNetworkModel.TypeId.UEL);
        networkModel.setNeuralNetwork(neuralNetwork);
        networkModel.setMinOutput(min);
        networkModel.setMaxOutput(max);

        double totalError = 0;
        if (neuralNetwork.getLearningRule() instanceof LMS) {
            final LMS learningRule = ((LMS) neuralNetwork.getLearningRule());
            totalError = learningRule.getTotalNetworkError();
            networkModel.setTotalError(totalError);
            networkModel.setCurrentIteration(learningRule.getCurrentIteration());
        }

        log.info("Model, {}", networkModel);

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
        outputs = unnormalizeData(outputs, networkModel.getMinOutput(), networkModel.getMaxOutput());

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

//    public static void main(String[] args) {
//        PredictionServiceImp serviceImp = new PredictionServiceImp();
//
//        CompoundConverter converter = new CompoundConverter();
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        session.beginTransaction();
//
//        Query compoundsQuery = session.createQuery("from Compound");
//        List<Compound> compounds = compoundsQuery.list();
//        List<CompoundDao> compoundDaoList = new ArrayList<>();
//        compounds.forEach(compound -> compoundDaoList.add(converter.convertToDao(compound)));
//
//        serviceImp.trainLELValue(compoundDaoList, TrainMethod.MOMENTUM_BACK_PROPAGATION);
//    }

    private NeuralNetwork getNeuralNetwork(final int inputSize, final TrainMethod trainMethod) {
        NeuralNetwork neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputSize, inputSize / 2, 1);

        LearningRule learningRule = new BackPropagation();
        switch (trainMethod) {
            case MOMENTUM_BACK_PROPAGATION:
                learningRule = new MomentumBackpropagation();
                break;
            case R_PROP:
                learningRule = new ResilientPropagation();
                break;
        }
        neuralNetwork.setLearningRule(learningRule);

        ((LMS) neuralNetwork.getLearningRule()).setMaxError(LEARN_MAX_ERROR);
        ((LMS) neuralNetwork.getLearningRule()).setLearningRate(LEARN_RATE);
        ((LMS) neuralNetwork.getLearningRule()).setMaxIterations(LEARN_ITERATION);

        neuralNetwork.setLabel(TransferFunctionType.SIGMOID.getTypeLabel());

        return neuralNetwork;
    }

}
