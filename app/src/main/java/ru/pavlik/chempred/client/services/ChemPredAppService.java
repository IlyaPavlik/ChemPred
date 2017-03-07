package ru.pavlik.chempred.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("message")
public interface ChemPredAppService extends RemoteService {

    String getMessage(String msg);

    String train(String text);

    class App {
        private static ChemPredAppServiceAsync ourInstance = GWT.create(ChemPredAppService.class);

        public static synchronized ChemPredAppServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
