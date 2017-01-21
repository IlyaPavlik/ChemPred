package client;

import client.base.BasePresenter;
import client.pages.main.MainBasePresenter;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

public class ChemPredController implements BasePresenter {

    private ChemPredAppServiceAsync serviceAsync;
    private HandlerManager handlerManager;

    public ChemPredController(ChemPredAppServiceAsync serviceAsync, HandlerManager handlerManager) {
        this.serviceAsync = serviceAsync;
        this.handlerManager = handlerManager;
    }

    public void go(HasWidgets container) {
        new MainBasePresenter().go(container);
    }
}
