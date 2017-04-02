package ru.pavlik.chempred.client.services.prediction;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.LinkDao;

import java.util.List;

public interface PredictionServiceAsync {
    void train(AsyncCallback<Double> error);

    void predict(List<LinkDao> links, AsyncCallback<Double> value);

    void predict(String smiles, AsyncCallback<Double> async);
}
