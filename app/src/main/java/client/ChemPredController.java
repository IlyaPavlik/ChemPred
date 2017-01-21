package client;

import client.base.Presenter;
import client.pages.main.MainPresenter;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

public class ChemPredController implements Presenter {

    private ChemPredAppServiceAsync serviceAsync;
    private HandlerManager handlerManager;

    public ChemPredController(ChemPredAppServiceAsync serviceAsync, HandlerManager handlerManager) {
        this.serviceAsync = serviceAsync;
        this.handlerManager = handlerManager;
    }

    public void go(HasWidgets container) {
        new MainPresenter().go(container);
    }
}
