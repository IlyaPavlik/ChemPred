package ru.pavlik.chempred.client.application.report;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.NeuralNetworkParamDao;
import ru.pavlik.chempred.client.services.compound.CompoundService;
import ru.pavlik.chempred.client.services.compound.CompoundServiceAsync;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.client.services.prediction.PredictionServiceAsync;
import ru.pavlik.chempred.client.utils.Utils;

import java.util.List;

public class ReportPresenter extends PresenterWidget<ReportPresenter.MyView> implements ReportUiHandler {

    private CompoundServiceAsync compoundService;
    private PredictionServiceAsync predictionService;

    interface MyView extends PopupView, HasUiHandlers<ReportUiHandler> {
        void showCompounds(List<CompoundDao> compounds);

        void showNeuralNetworkParams(NeuralNetworkParamDao neuralNetworkParam);
    }

    public ReportPresenter(EventBus eventBus, MyView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);

        compoundService = CompoundService.Service.getInstance();
        predictionService = PredictionService.Service.getInstance();
    }

    @Override
    public void loadData() {
        compoundService.getCompounds(new AsyncCallback<List<CompoundDao>>() {
            @Override
            public void onFailure(Throwable caught) {
                Utils.console(caught);
            }

            @Override
            public void onSuccess(List<CompoundDao> result) {
                predictionService.predictCompounds(result, new AsyncCallback<List<CompoundDao>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Utils.console(caught);
                    }

                    @Override
                    public void onSuccess(List<CompoundDao> result) {
                        getView().showCompounds(result);
                    }
                });
            }
        });

        predictionService.loadNeuralNetworkParams(new AsyncCallback<NeuralNetworkParamDao>() {
            @Override
            public void onFailure(Throwable caught) {
                Utils.console(caught);
            }

            @Override
            public void onSuccess(NeuralNetworkParamDao result) {
                Utils.console(result);
                getView().showNeuralNetworkParams(result);
            }
        });
    }
}
