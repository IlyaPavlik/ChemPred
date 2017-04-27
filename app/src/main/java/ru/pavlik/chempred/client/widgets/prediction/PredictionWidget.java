package ru.pavlik.chempred.client.widgets.prediction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Span;

public class PredictionWidget extends Composite {

    public interface OnPredictClickListener {
        void onClick();
    }

    @UiField
    Span lowRatio;
    @UiField
    Span highRatio;
    @UiField
    Button predictButton;

    private OnPredictClickListener onPredictClickListener;

    interface PredictionBinder extends UiBinder<Widget, PredictionWidget> {
    }

    private static UiBinder<Widget, PredictionWidget> binder = GWT.create(PredictionBinder.class);

    public PredictionWidget() {
        initWidget(binder.createAndBindUi(this));
    }

    public void setOnPredictClickListener(OnPredictClickListener onPredictClickListener) {
        this.onPredictClickListener = onPredictClickListener;
    }

    public void setLowRatio(String lowRatio) {
        this.lowRatio.setText(lowRatio);
    }

    public void setHighRatio(String highRatio) {
        this.highRatio.setText(highRatio);
    }

    @UiHandler("predictButton")
    public void onPredictClick(ClickEvent event) {
        if (onPredictClickListener != null) {
            onPredictClickListener.onClick();
        }
    }

    public void clear() {
        this.lowRatio.setText("---");
        this.highRatio.setText("---");
    }
}
