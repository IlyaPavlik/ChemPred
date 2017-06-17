package ru.pavlik.chempred.client.application.report;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import ru.pavlik.chempred.client.application.base.BasePopupView;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;
import ru.pavlik.chempred.client.utils.NumberUtils;

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
    HTML currentIterationField;
    @UiField
    HTML totalErrorField;

    @UiField
    TabListItem lelTab;
    @UiField
    TabListItem uelTab;

    @Inject
    public ReportView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        lelTab.addClickHandler(event -> {
            uelTab.setActive(false);
            lelTab.setActive(true);

            clearData();
            init(true);
        });

        uelTab.addClickHandler(event -> {
            uelTab.setActive(true);
            lelTab.setActive(false);

            clearData();
            init(false);
        });
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        clearData();
        init(true);
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
        iterationField.setText(String.valueOf(neuralNetworkParam.getTotalIterations()));
        maxErrorField.setText(String.valueOf(neuralNetworkParam.getMaxError()));
        rateField.setText(String.valueOf(neuralNetworkParam.getRate()));

        inputsField.setText(String.valueOf(neuralNetworkParam.getInputSize()));
        outputsField.setText(String.valueOf(neuralNetworkParam.getOutputSize()));
        currentIterationField.setText(String.valueOf(neuralNetworkParam.getCurrentIterations()));

        final double error = NumberUtils.round(neuralNetworkParam.getTotalError(), 8);
        totalErrorField.setText(String.valueOf(error));
    }

    private void init(final boolean useLEL) {
        initTableColumns(useLEL);
        getUiHandlers().loadData(useLEL);
    }

    private void initTableColumns(final boolean useLEL) {
        TextColumn<CompoundDao> nameColumn = new TextColumn<CompoundDao>() {
            @Override
            public String getValue(CompoundDao object) {
                return object.getName();
            }
        };
        table.addColumn(nameColumn, "Наименование");

        TextColumn<CompoundDao> exposureLimit = new TextColumn<CompoundDao>() {
            @Override
            public String getValue(CompoundDao compound) {
                return String.valueOf(useLEL
                        ? compound.getLowFactor()
                        : compound.getUpperFactor());
            }
        };
        table.addColumn(exposureLimit, useLEL ? "НКПВ" : "ВКПВ");

        TextColumn<CompoundDao> exposureLimitPredict = new TextColumn<CompoundDao>() {
            @Override
            public String getValue(CompoundDao compound) {
                return NumberFormat.getFormat("#.##").format(useLEL
                        ? compound.getLowFactorPrediction()
                        : compound.getUpperFactorPrediction());
            }
        };
        table.addColumn(exposureLimitPredict, useLEL ? "НКПВ (Спрогнозированное)" : "ВКПВ (Спрогнозированное)");
    }

    private void clearData() {
        while (table.getColumnCount() > 0) {
            table.removeColumn(0);
        }
    }
}