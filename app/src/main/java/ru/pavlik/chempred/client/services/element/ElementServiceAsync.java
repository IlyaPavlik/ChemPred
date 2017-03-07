package ru.pavlik.chempred.client.services.element;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.ElementDao;

import java.util.List;

public interface ElementServiceAsync {
    void getElements(AsyncCallback<List<ElementDao>> async);

    void getElement(String sign, AsyncCallback<ElementDao> async);
}
