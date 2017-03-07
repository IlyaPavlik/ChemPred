package ru.pavlik.chempred.client.services.element;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.pavlik.chempred.client.model.dao.ElementDao;

import java.util.List;

@RemoteServiceRelativePath("element")
public interface ElementService extends RemoteService {

    List<ElementDao> getElements();

    ElementDao getElement(String sign);

    class Service {
        private static ElementServiceAsync ourInstance = GWT.create(ElementService.class);

        public static synchronized ElementServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
