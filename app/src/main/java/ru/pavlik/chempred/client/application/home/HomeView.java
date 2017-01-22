package ru.pavlik.chempred.client.application.home;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

    @UiField(provided = true)
    Canvas canvas;

    interface Binder extends UiBinder<Widget, HomeView> {
    }

    @Inject
    HomeView(Binder binder) {
        canvas = Canvas.createIfSupported();
        canvas.setStyleName("main-canvas");
        initWidget(binder.createAndBindUi(this));
    }
}
