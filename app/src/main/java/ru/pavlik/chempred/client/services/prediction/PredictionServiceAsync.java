package ru.pavlik.chempred.client.services.prediction;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;

import java.util.List;

public interface PredictionServiceAsync {
    void train(AsyncCallback<Double> error);

    void predict(List<LinkDao> links, AsyncCallback<Double> value);

    void predict(String smiles, AsyncCallback<Double> async);

    void train(List<CompoundDao> compounds, AsyncCallback<Double> async);

    void predictCompounds(List<CompoundDao> compounds, AsyncCallback<List<CompoundDao>> async);

    void loadNeuralNetworkParams(AsyncCallback<NeuralNetworkParamDao> async);
}
