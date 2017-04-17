package ru.pavlik.chempred.client.widgets.prediction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PredictionWidget extends Composite {

    interface PredictionBinder extends UiBinder<Widget, PredictionWidget> {
    }

    private static UiBinder<Widget, PredictionWidget> binder = GWT.create(PredictionBinder.class);

    public PredictionWidget() {
        initWidget(binder.createAndBindUi(this));
    }
}
