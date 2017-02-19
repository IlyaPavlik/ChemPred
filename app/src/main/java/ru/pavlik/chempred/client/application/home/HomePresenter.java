package ru.pavlik.chempred.client.application.home;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallStartEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallSucceedEvent;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import ru.pavlik.chempred.client.place.NameTokens;
import ru.pavlik.chempred.client.utils.Utils;

import javax.inject.Inject;

public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy> {
    interface MyView extends View {
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
    }

    @ProxyEvent
    public void onStartEvent(AsyncCallStartEvent asyncCallStartEvent) {
        Utils.console("Start");
    }

    @ProxyEvent
    public void onSuccessEvent(AsyncCallSucceedEvent asyncCallSucceedEvent) {
        Utils.console("Success");
    }

    @ProxyEvent
    public void onFailEvent(AsyncCallFailEvent asyncCallFailEvent) {
        Utils.console("Fail");
    }
}
