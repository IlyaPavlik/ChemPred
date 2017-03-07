package ru.pavlik.chempred.client.application.home;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.place.NameTokens;
import ru.pavlik.chempred.client.services.element.ElementService;
import ru.pavlik.chempred.client.services.element.ElementServiceAsync;
import ru.pavlik.chempred.client.utils.Utils;

import javax.inject.Inject;

public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy> implements PresenterUiHandler {

    private ElementServiceAsync elementService;

    interface MyView extends View, HasUiHandlers<PresenterUiHandler> {
        void setElement(ElementDao element);
    }

    @ProxyStandard
    @NameToken(NameTokens.HOME)
    interface MyProxy extends ProxyPlace<HomePresenter> {
    }

    @Inject
    HomePresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy) {
        super(eventBus, view, proxy, RevealType.RootLayout);
        getView().setUiHandlers(this);

        elementService = ElementService.Service.getInstance();
    }

    @Override
    public void handleElementClick(String sign) {
        elementService.getElement(sign, new AsyncCallback<ElementDao>() {
            @Override
            public void onFailure(Throwable caught) {
                Utils.console(caught);
            }

            @Override
            public void onSuccess(ElementDao result) {
                getView().setElement(result);
            }
        });
    }
}
