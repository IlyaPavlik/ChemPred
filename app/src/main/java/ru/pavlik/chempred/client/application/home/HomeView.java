package ru.pavlik.chempred.client.application.home;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import ru.pavlik.chempred.client.widgets.DrawPanelWidget;

import javax.inject.Inject;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

    @UiField
    DrawPanelWidget panel;
//    DrawPanelWidget panel;

    interface Binder extends UiBinder<Widget, HomeView> {
    }

    @Inject
    HomeView(Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        Scheduler.get().scheduleDeferred(() ->
                panel.init(panel.getOffsetWidth(), panel.getOffsetHeight()));

//        panel.init(960, 400);
    }
}
