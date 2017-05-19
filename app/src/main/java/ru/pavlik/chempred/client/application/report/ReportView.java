package ru.pavlik.chempred.client.application.report;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import ru.pavlik.chempred.client.application.base.BasePopupView;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;

import javax.inject.Inject;
import java.util.List;

public class ReportView extends BasePopupView<ReportUiHandler> implements ReportPresenter.MyView {

    interface Binder extends UiBinder<PopupPanel, ReportView> {
    }

    @UiField
    CellTable<CompoundDao> table;

    @UiField
    HTML activationField;
    @UiField
    HTML inputsField;
    @UiField
    HTML outputsField;
    @UiField
    HTML iterationField;
    @UiField
    HTML maxErrorField;
    @UiField
    HTML rateField;

    @UiField
    HTML totalErrorField;

    @Inject
    public ReportView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        while (table.getColumnCount() > 0) {
            table.removeColumn(0);
        }
        initTableColumns();
        getUiHandlers().loadData();
    }

    @Override
    public void showCompounds(List<CompoundDao> compounds) {
        ListDataProvider<CompoundDao> listDataProvider = new ListDataProvider<>(compounds);
        listDataProvider.addDataDisplay(table);
        table.setPageSize(compounds.size());
    }

    @Override
    public void showNeuralNetworkParams(NeuralNetworkParamDao neuralNetworkParam) {
        activationField.setText(neuralNetworkParam.getActivationFunction());
        inputsField.setText(String.valueOf(neuralNetworkParam.getInputSize()));
        outputsField.setText(String.valueOf(neuralNetworkParam.getOutputSize()));
        iterationField.setText(String.valueOf(neuralNetworkParam.getIterations()));
        maxErrorField.setText(String.valueOf(neuralNetworkParam.getMaxError()));
        rateField.setText(String.valueOf(neuralNetworkParam.getRate()));

        totalErrorField.setText(String.valueOf(neuralNetworkParam.getTotalError()));
    }

    private void initTableColumns() {
        TextColumn<CompoundDao> nameColumn = new TextColumn<CompoundDao>() {
            @Override
            public String getValue(CompoundDao object) {
                return object.getName();
            }
        };
        table.addColumn(nameColumn, "Наименование");

        TextColumn<CompoundDao> lowFactor = new TextColumn<CompoundDao>() {
            @Override
            public String getValue(CompoundDao object) {
                return String.valueOf(object.getLowFactor());
            }
        };
        table.addColumn(lowFactor, "НКПРП");

        TextColumn<CompoundDao> lowFactorPredict = new TextColumn<CompoundDao>() {
            @Override
            public String getValue(CompoundDao object) {
                return NumberFormat.getFormat("#.##").format(object.getLowFactorPrediction());
            }
        };
        table.addColumn(lowFactorPredict, "НКПРП (Спрогнозированное)");
    }
}