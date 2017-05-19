package ru.pavlik.chempred.client.widgets.loader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoaderWidget extends Composite {

    interface LoaderWidgetBinder extends UiBinder<Widget, LoaderWidget> {
    }

    private static UiBinder<Widget, LoaderWidget> binder = GWT.create(LoaderWidgetBinder.class);

    public LoaderWidget() {
        initWidget(binder.createAndBindUi(this));
    }
}
