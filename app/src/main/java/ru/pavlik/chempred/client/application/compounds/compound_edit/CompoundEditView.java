package ru.pavlik.chempred.client.application.compounds.compound_edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import ru.pavlik.chempred.client.application.base.BasePopupView;
import ru.pavlik.chempred.client.model.dao.CompoundDao;

import javax.inject.Inject;

public class CompoundEditView extends BasePopupView<CompoundEditUiHandler> implements CompoundEditPresenter.MyView {

    @UiField
    TextBox name;
    @UiField
    FormGroup smilesGroup;
    @UiField
    TextBox smiles;
    @UiField
    TextBox lel;
    @UiField
    TextBox uel;
    @UiField
    Button lelPredict;
    @UiField
    Button uelPredict;

    interface Binder extends UiBinder<PopupPanel, CompoundEditView> {
    }

    @Inject
    public CompoundEditView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        lel.addKeyPressHandler(onlyNumberHandler(lel));
        uel.addKeyPressHandler(onlyNumberHandler(uel));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        getUiHandlers().loadSmiles();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        name.setText(null);
        smiles.setText(null);

        lel.setEnabled(true);
        lelPredict.setEnabled(true);
        lel.setText(null);

        uel.setEnabled(true);
        uelPredict.setEnabled(true);
        uel.setText(null);
    }

    @Override
    public void showSmiles(String smiles) {
        this.smiles.setText(smiles);
    }

    @Override
    public void showLEL(Double lelValue) {
        lel.setEnabled(true);
        lelPredict.setEnabled(true);
        if (lelValue != null) {
            lel.setText(String.valueOf(lelValue));
        }
    }

    @Override
    public void showUEL(Double uelValue) {
        uel.setEnabled(true);
        uelPredict.setEnabled(true);
        if (uelValue != null) {
            uel.setText(String.valueOf(uelValue));
        }
    }

    private KeyPressHandler onlyNumberHandler(final TextBox textBox) {
        return event -> {
            if (event.getNativeEvent().getKeyCode() != KeyCodes.KEY_DELETE &&
                    event.getNativeEvent().getKeyCode() != KeyCodes.KEY_BACKSPACE &&
                    event.getNativeEvent().getKeyCode() != KeyCodes.KEY_LEFT &&
                    event.getNativeEvent().getKeyCode() != KeyCodes.KEY_RIGHT) {
                String c = event.getCharCode() + "";
                if (RegExp.compile("[^0-9]").test(c))
                    textBox.cancelKey();
            }
        };
    }

    @UiHandler("lelPredict")
    public void onPredictLEL(ClickEvent event) {
        final String smilesValue = smiles.getText();
        if (!smilesValue.isEmpty()) {
            smilesGroup.setValidationState(ValidationState.NONE);
            lel.setEnabled(false);
            lelPredict.setEnabled(false);
            getUiHandlers().handlePredictLEL(smilesValue);
        } else {
            smilesGroup.setValidationState(ValidationState.ERROR);
        }
    }

    @UiHandler("uelPredict")
    public void onPredictUEL(ClickEvent event) {
        final String smilesValue = smiles.getText();
        if (!smilesValue.isEmpty()) {
            smilesGroup.setValidationState(ValidationState.NONE);
            uel.setEnabled(false);
            uelPredict.setEnabled(false);
            getUiHandlers().handlePredictUEL(smilesValue);
        } else {
            smilesGroup.setValidationState(ValidationState.ERROR);
        }
    }

    @UiHandler("add")
    public void onAddCompound(ClickEvent clickEvent) {
        if (!canAdd()) {
            Notify.notify("Все поля должны быть заполнены", NotifyType.DANGER);
        }

        CompoundDao compound = new CompoundDao();
        compound.setName(name.getText());
        compound.setSmiles(smiles.getText());
        compound.setLowFactor(Double.parseDouble(lel.getText()));
        compound.setUpperFactor(Double.parseDouble(uel.getText()));
        getUiHandlers().handleAddCompound(compound);
        hide();
    }

    private boolean canAdd() {
        return !name.getText().isEmpty()
                && !smiles.getText().isEmpty()
                && !lel.getText().isEmpty()
                && !uel.getText().isEmpty();
    }
}
