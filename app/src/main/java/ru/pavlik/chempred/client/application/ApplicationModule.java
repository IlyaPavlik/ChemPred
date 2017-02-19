package ru.pavlik.chempred.client.application;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import ru.pavlik.chempred.client.application.home.HomeModule;

public class ApplicationModule extends AbstractPresenterModule {
    @Override
    protected void configure() {

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);

        install(new HomeModule());
    }
}
