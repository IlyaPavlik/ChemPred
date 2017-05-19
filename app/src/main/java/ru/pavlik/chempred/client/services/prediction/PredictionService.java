package ru.pavlik.chempred.client.services.prediction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;

import java.util.List;

@RemoteServiceRelativePath("prediction")
public interface PredictionService extends RemoteService {

    double predict(List<LinkDao> links);

    double predict(String smiles);

    List<CompoundDao> predictCompounds(List<CompoundDao> compounds);

    double train();

    double train(List<CompoundDao> compounds);

    NeuralNetworkParamDao loadNeuralNetworkParams();

    class Service {
        private static PredictionServiceAsync ourInstance = GWT.create(PredictionService.class);

        public static synchronized PredictionServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
