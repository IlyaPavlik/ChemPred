package ru.pavlik.chempred.client.model.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.utils.DialogUtils;
import ru.pavlik.chempred.client.utils.Utils;

public abstract class ErrorHandlerCallback<T> implements AsyncCallback<T> {

    @Override
    public void onFailure(Throwable caught) {
        Utils.console(caught);
        DialogUtils.showErrorDialog("Error", caught.getMessage());
    }
}
