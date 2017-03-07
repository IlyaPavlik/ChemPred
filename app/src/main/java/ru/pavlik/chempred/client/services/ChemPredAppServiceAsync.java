package ru.pavlik.chempred.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChemPredAppServiceAsync {
    void getMessage(String msg, AsyncCallback<String> async);

    void train(String text, AsyncCallback<String> async);
}
