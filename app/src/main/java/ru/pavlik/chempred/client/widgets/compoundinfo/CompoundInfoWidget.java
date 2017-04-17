package ru.pavlik.chempred.client.widgets.compoundinfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.html.Span;
import ru.pavlik.chempred.client.utils.TextUtils;

public class CompoundInfoWidget extends Composite {

    interface CompoundInfoBinder extends UiBinder<Widget, CompoundInfoWidget> {
    }

    private static UiBinder<Widget, CompoundInfoWidget> binder = GWT.create(CompoundInfoBinder.class);

    private static final String UNDEFINED_COMPOUND_FIELD = "Неизвестное соединение";
    private static final String EMPTY_FIELD = "---";

    @UiField
    Span compoundName;
    @UiField
    Span brutto;
    @UiField
    Span smiles;

    public CompoundInfoWidget() {
        initWidget(binder.createAndBindUi(this));
    }

    public void setCompoundName(String compoundName) {
        if (!TextUtils.isEmpty(compoundName)) {
            this.compoundName.setText(compoundName);
        } else {
            this.compoundName.setText(UNDEFINED_COMPOUND_FIELD);
        }
    }

    public void setBrutto(String brutto) {
        if (!TextUtils.isEmpty(brutto)) {
            this.brutto.setText(brutto);
        } else {
            this.brutto.setText(EMPTY_FIELD);
        }
    }

    public void setSmiles(String smiles) {
        if (!TextUtils.isEmpty(smiles)) {
            this.smiles.setText(smiles);
        } else {
            this.smiles.setText(EMPTY_FIELD);
        }
    }

    public void clear() {
        this.compoundName.setText(UNDEFINED_COMPOUND_FIELD);
        this.brutto.setText(EMPTY_FIELD);
        this.smiles.setText(EMPTY_FIELD);
    }
}
