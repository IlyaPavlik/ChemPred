package ru.pavlik.chempred.client.application.compounds.compound_edit;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.rpc.ErrorHandlerCallback;
import ru.pavlik.chempred.client.services.compound.CompoundService;
import ru.pavlik.chempred.client.services.compound.CompoundServiceAsync;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.client.services.prediction.PredictionServiceAsync;
import ru.pavlik.chempred.client.utils.NumberUtils;

public class CompoundEditPresenter extends PresenterWidget<CompoundEditPresenter.MyView> implements CompoundEditUiHandler {

    private CompoundServiceAsync compoundService;
    private PredictionServiceAsync predictionService;

    private String smiles;

    interface MyView extends PopupView, HasUiHandlers<CompoundEditUiHandler> {
        void showSmiles(String smiles);

        void showLEL(Double lel);

        void showUEL(Double uel);
    }

    public CompoundEditPresenter(EventBus eventBus, MyView view, String smiles) {
        super(eventBus, view);
        getView().setUiHandlers(this);

        compoundService = CompoundService.Service.getInstance();
        predictionService = PredictionService.Service.getInstance();

        this.smiles = smiles;
    }

    @Override
    public void loadSmiles() {
        getView().showSmiles(smiles);
    }

    @Override
    public void handlePredictLEL(String smiles) {
        predictionService.predictLEL(smiles, new ErrorHandlerCallback<Double>() {
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                getView().showLEL(null);
            }

            @Override
            public void onSuccess(Double result) {
                getView().showLEL(NumberUtils.round(result, 2));
            }
        });
    }

    @Override
    public void handlePredictUEL(String smiles) {
        predictionService.predictUEL(smiles, new ErrorHandlerCallback<Double>() {
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                getView().showUEL(null);
            }

            @Override
            public void onSuccess(Double result) {
                getView().showUEL(NumberUtils.round(result, 2));
            }
        });
    }

    @Override
    public void handleAddCompound(CompoundDao newCompound) {
        compoundService.addNewCompound(newCompound, new ErrorHandlerCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Notify.notify("Соединение добавлено");
            }
        });
    }
}
