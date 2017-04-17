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
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.server.model.Compound;
import ru.pavlik.chempred.server.model.NeuralNetworkModel;
import ru.pavlik.chempred.server.utils.HibernateUtil;
import ru.pavlik.chempred.server.utils.SmilesUtils;

import java.util.List;

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
    public double predict(List<LinkDao> links) {
        String smiles = SmilesUtils.parseStructure(links);
        return predict(smiles);
    }

    @Override
    public double predict(String smiles) {
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        BCUTDescriptor bcutDescriptor = new BCUTDescriptor();
        IAtomContainer atomContainer;
        try {
            atomContainer = smilesParser.parseSmiles(smiles);
        } catch (InvalidSmilesException e) {
            log.error("Error occurred while parse smile: ", e);
            return -1;
        }
        DescriptorValue calculate = bcutDescriptor.calculate(atomContainer);
        DoubleArrayResult arrayResult = (DoubleArrayResult) calculate.getValue();
        double[] arrayInputs = new double[arrayResult.length()];
        for (int j = 0; j < arrayResult.length(); j++) {
            arrayInputs[j] = arrayResult.get(j);
        }

        arrayInputs = normalizeData(arrayInputs);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from NeuralNetworkModel");
        NeuralNetworkModel networkModel = (NeuralNetworkModel) query.list().get(0);
        session.getTransaction().commit();

        networkModel.getNeuralNetwork().setInput(arrayInputs);
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

    public static void main(String[] args) {
        PredictionServiceImp serviceImp = new PredictionServiceImp();
        serviceImp.train();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Compound");
        List<Compound> compounds = query.list();
        session.getTransaction().commit();

        for (Compound compound : compounds) {
            double output = serviceImp.predict(compound.getSmiles());
            System.out.println("!!" + compound.getName() + " : " + output);
        }
    }

//    public Map<Compound, Map<String, Integer>> getCompoundDescriptors(Set<String> descriptors) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        session.beginTransaction();
//        Query query = session.createQuery("from Compound");
//        List<Compound> compounds = query.list();
//
//        Map<Compound, Map<String, Integer>> compoundDescriptors = new HashMap<>();
//
//        for (Compound compound : compounds) {
//            String compound = compound.getSmiles();
//            List<String> smilesDescriptors = getDescriptors(compound);
//            descriptors.addAll(smilesDescriptors);
//
//            Map<String, Integer> countDescriptors = new TreeMap<>();
//            for (String smilesDescriptor : smilesDescriptors) {
//                int count = 0;
//                for (String descriptor : smilesDescriptors) {
//                    if (smilesDescriptor.equals(descriptor)) {
//                        count++;
//                    }
//                }
//                countDescriptors.put(smilesDescriptor, count);
//            }
//            compoundDescriptors.put(compound, countDescriptors);
//        }
//
//        //fill empty descriptors
//        for (String descriptor : descriptors) {
//            for (Map.Entry<Compound, Map<String, Integer>> compoundMapEntry : compoundDescriptors.entrySet()) {
//                Map<String, Integer> countDescriptors = compoundMapEntry.getValue();
//                if (!countDescriptors.containsKey(descriptor)) {
//                    countDescriptors.put(descriptor, 0);
//                }
//            }
//        }
//
//        return compoundDescriptors;
//    }
//
//    public List<String> getDescriptors(String compound) {
//        List<String> descriptors = new ArrayList<>();
//        StructureDao structureDao = SmilesUtils.parseSmiles(daoElements, compound);
//
//        //correct valence
//        for (LinkDao linkDao : structureDao.getLinks()) {
//            ElementDao source = linkDao.getElementSource();
//            ElementDao target = linkDao.getElementTarget();
//            source.setValence(source.getValence() - linkDao.getLinkType().getWeight());
//            target.setValence(target.getValence() - linkDao.getLinkType().getWeight());
//        }
//
//        for (ElementDao elementDao : structureDao.getElements()) {
//            StringBuilder stringBuilder = new StringBuilder(elementDao.getSymbol());
//            int valence = elementDao.getValence();
//            if (valence > 0) {
//                stringBuilder.append("H");
//            }
//            if (valence > 1) {
//                stringBuilder.append(String.valueOf(valence));
//            }
//            descriptors.add(stringBuilder.toString());
//        }
//
//        return descriptors;
//    }

//    public static void main(String[] args) {
//        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
//        try {
////            IAtomContainer m = smilesParser.parseSmiles("CC=CNS");
//
////            CircularFingerprinter fingerprinter = new CircularFingerprinter(CircularFingerprinter.CLASS_ECFP2, 512);
////            IBitFingerprint bitFingerprint = fingerprinter.getBitFingerprint(m);
////            System.out.println(bitFingerprint.asBitSet().toString());
////            System.out.println(Arrays.toString(bitFingerprint.asBitSet().toByteArray()));
//
//            //Несколько параметров, но реагируют только на углерод
////            MDEDescriptor mdeDescriptor = new MDEDescriptor();
////            DescriptorValue descriptorValue = mdeDescriptor.calculate(m);
////            System.out.println(descriptorValue.getValue());
//
//            //Всего три параметра, но реагируют на все
////            ALOGPDescriptor alogpDescriptor = new ALOGPDescriptor();
////            DescriptorValue calculate = alogpDescriptor.calculate(m);
////            System.out.println(calculate.getValue());
//
//            //only for carbon
////            CarbonTypesDescriptor carbonTypesDescriptor = new CarbonTypesDescriptor();
////            DescriptorValue descriptorValue = carbonTypesDescriptor.calculate(m);
////            System.out.println(descriptorValue.getValue());
//
////            BCUTDescriptor bcutDescriptor = new BCUTDescriptor();
////            DescriptorValue calculate = bcutDescriptor.calculate(m);
////            System.out.println(calculate.getValue());
//
//
//            int inputSize = 6;
//            NeuralNetwork neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputSize, inputSize / 2, inputSize / 2, 1);
//            ((LMS) neuralNetwork.getLearningRule()).setMaxError(LEARN_MAX_ERROR);
//            ((LMS) neuralNetwork.getLearningRule()).setLearningRate(LEARN_RATE);
//            ((LMS) neuralNetwork.getLearningRule()).setMaxIterations(LEARN_ITERATION);
//
//            DataSet dataSet = new DataSet(inputSize, 1);
//
//            Session session = HibernateUtil.getSessionFactory().openSession();
//            session.beginTransaction();
//            Query query = session.createQuery("from Compound");
//            List<Compound> compounds = query.list();
//
//            double[][] inputs = new double[compounds.size()][6];
//            double[] outputs = new double[compounds.size()];
//
//            BCUTDescriptor bcutDescriptor = new BCUTDescriptor();
//            for (int i = 0; i < compounds.size(); i++) {
//                Compound compound = compounds.get(i);
//                IAtomContainer atomContainer = smilesParser.parseSmiles(compound.getSmiles());
//                DescriptorValue calculate = bcutDescriptor.calculate(atomContainer);
//                DoubleArrayResult arrayResult = (DoubleArrayResult) calculate.getValue();
//                double[] arrayInputs = new double[arrayResult.length()];
//                for (int j = 0; j < arrayResult.length(); j++) {
//                    arrayInputs[j] = arrayResult.get(j);
//                }
//                inputs[i] = arrayInputs;
//                outputs[i] = compound.getExperimentalFactor();
//            }
//
//            inputs = normalizeData(inputs);
//            outputs = normalizeData(outputs);
//
//            for (int i = 0; i < inputs.length; i++) {
//                dataSet.addRow(inputs[i], new double[]{outputs[i]});
//            }
//            neuralNetwork.learn(dataSet);
//
//            int i = 0;
//            for (DataSetRow dataSetRow : dataSet.getRows()) {
//                neuralNetwork.setInput(dataSetRow.getInput());
//                neuralNetwork.calculate();
//
//                double[] networkOutput = neuralNetwork.getOutput();
//                outputs[i++] = networkOutput[0];
//            }
//            outputs = unnormalizeData(outputs, 0.47, 17);
//
//            int j = 0;
//            for (double v : outputs) {
//                System.out.println(compounds.get(j++).getName());
//                System.out.println(String.valueOf(v));
//            }
//            System.out.println(((LMS) neuralNetwork.getLearningRule()).getTotalNetworkError());
//
//        } catch (CDKException e) {
//            e.printStackTrace();
//        }
//    }
}
