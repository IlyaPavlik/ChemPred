package ru.pavlik.chempred.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import ru.pavlik.chempred.client.services.ChemPredAppService;
import ru.pavlik.chempred.server.model.Element;
import ru.pavlik.chempred.server.utils.HibernateUtil;

import java.util.List;

public class ChemPredServiceImp extends RemoteServiceServlet implements ChemPredAppService {

    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }

    public String train(String text) {
//        tensorflow.Tensor inputs = new tensorflow.Tensor(
//                tensorflow.DT_FLOAT,
//                new tensorflow.TensorShape(2, 5)
//        );
//
//        FloatBuffer x = inputs.createBuffer();
//        x.put(new float[]{-6.0f, 22.0f, 383.0f, 27.781754111198122f, -6.5f});
//        x.put(new float[]{66.0f, 22.0f, 2422.0f, 45.72160947712418f, 0.4f});
//
//        tensorflow.Tensor keepall = new tensorflow.Tensor(
//                tensorflow.DT_FLOAT, new tensorflow.TensorShape(2, 1));
//        ((FloatBuffer) keepall.createBuffer()).put(new float[]{1f, 1f});
//
//        tensorflow.TensorVector outputs = new tensorflow.TensorVector();
//// to predict each time, pass in values for placeholders
//        outputs.resize(0);
//        final tensorflow.Session session = new tensorflow.Session(new tensorflow.SessionOptions());
//        tensorflow.Status s = session.Run(
//                new tensorflow.StringTensorPairVector(new String[]{"Placeholder", "Placeholder_2"}, new tensorflow.Tensor[]{inputs, keepall}),
//                new tensorflow.StringVector("Sigmoid"), new tensorflow.StringVector(),
//                outputs
//        );
//        if (!s.ok()) {
//            throw new RuntimeException(s.error_message().getString());
//        }
//// this is how you get back the predicted value from outputs
//        FloatBuffer output = outputs.get(0).createBuffer();
//        for (int k = 0; k < output.limit(); ++k) {
//            System.out.println("prediction=" + output.get(k));
//        }

//        List<ClassifierInstance> irisDataset = null;
//        try {
//            irisDataset = PredictiveAccuracyTests.loadIrisDataset();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        final RandomDecisionForest randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()
//                // The default isn't desirable here because this dataset has so few attributes
//                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.2)))
//                .buildPredictiveModel(irisDataset);
//
//        AttributesMap attributes = new AttributesMap();
//        attributes.put("sepal-length", 5.84);
//        attributes.put("sepal-width", 3.05);
//        attributes.put("petal-length", 3.76);
//        attributes.put("petal-width", 1.20);
//        System.out.println("Prediction: " + randomForest.predict(attributes));
//        for (ClassifierInstance instance : irisDataset) {
//            System.out.println("classification: " + randomForest.getClassificationByMaxProb(instance.getAttributes()));
//        }


        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Element");

        List<Element> elementList = query.list();
        return "" + elementList.size();
    }
}
