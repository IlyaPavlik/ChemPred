package ru.pavlik.chempred.client.services.prediction;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

public interface PredictionServiceAsync {

    void predictCompounds(List<CompoundDao> compounds, AsyncCallback<List<CompoundDao>> async);

    void loadNeuralNetworkParams(AsyncCallback<NeuralNetworkParamDao> async);

    void trainLELValue(List<CompoundDao> compounds, AsyncCallback<Double> async);

    void trainUELValue(List<CompoundDao> compounds, AsyncCallback<Double> async);

    void predictLEL(String smiles, AsyncCallback<Double> async);

    void predictUEL(String smiles, AsyncCallback<Double> async);

    void predictUEL(StructureDao structure, AsyncCallback<Double> async);

    void predictLEL(StructureDao structure, AsyncCallback<Double> async);
}
