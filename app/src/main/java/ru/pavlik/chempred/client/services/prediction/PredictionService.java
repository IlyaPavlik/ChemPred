package ru.pavlik.chempred.client.services.prediction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

@RemoteServiceRelativePath("prediction")
public interface PredictionService extends RemoteService {

    double predictLEL(StructureDao structure);

    double predictUEL(StructureDao structure);

    double predictLEL(String smiles);

    double predictUEL(String smiles);

    List<CompoundDao> predictAllCompounds(boolean useLEL);

    double trainLELValue(List<CompoundDao> compounds);

    double trainUELValue(List<CompoundDao> compounds);

    NeuralNetworkParamDao loadNeuralNetworkParams(boolean useLEL);

    class Service {
        private static PredictionServiceAsync ourInstance = GWT.create(PredictionService.class);

        public static synchronized PredictionServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
