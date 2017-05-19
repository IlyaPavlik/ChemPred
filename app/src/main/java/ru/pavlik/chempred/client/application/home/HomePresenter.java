package ru.pavlik.chempred.client.application.home;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import ru.pavlik.chempred.client.application.compounds.CompoundsPresenter;
import ru.pavlik.chempred.client.application.compounds.CompoundsView;
import ru.pavlik.chempred.client.application.periodictable.PeriodicTablePresenter;
import ru.pavlik.chempred.client.application.periodictable.PeriodicTableView;
import ru.pavlik.chempred.client.application.report.ReportPresenter;
import ru.pavlik.chempred.client.application.report.ReportView;
import ru.pavlik.chempred.client.application.train.TrainPresenter;
import ru.pavlik.chempred.client.application.train.TrainView;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.model.events.BuildCompoundEvent;
import ru.pavlik.chempred.client.model.events.SelectElementEvent;
import ru.pavlik.chempred.client.model.events.UpdateStructureEvent;
import ru.pavlik.chempred.client.model.rpc.ErrorHandlerCallback;
import ru.pavlik.chempred.client.place.NameTokens;
import ru.pavlik.chempred.client.services.compound.CompoundService;
import ru.pavlik.chempred.client.services.compound.CompoundServiceAsync;
import ru.pavlik.chempred.client.services.element.ElementService;
import ru.pavlik.chempred.client.services.element.ElementServiceAsync;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.client.services.prediction.PredictionServiceAsync;

import javax.inject.Inject;
import java.util.List;

public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy> implements PresenterUiHandler {

    private ElementServiceAsync elementService;
    private CompoundServiceAsync compoundService;
    private PredictionServiceAsync predictionService;

    @Inject
    PeriodicTableView periodicTableView;
    @Inject
    CompoundsView compoundsView;
    @Inject
    TrainView trainView;
    @Inject
    ReportView reportView;

    interface MyView extends View, HasUiHandlers<PresenterUiHandler> {
        void setElement(ElementDao element);

        void setStructure(StructureDao structure);

        void showCompoundInfo(CompoundDao compoundDao);

        void showPredictionData(double lowRatio);
    }

    @ProxyStandard
    @NameToken(NameTokens.HOME)
    interface MyProxy extends ProxyPlace<HomePresenter> {
    }

    @Inject
    HomePresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy) {
        super(eventBus, view, proxy, RevealType.RootLayout);
        getView().setUiHandlers(this);

        elementService = ElementService.Service.getInstance();
        compoundService = CompoundService.Service.getInstance();
        predictionService = PredictionService.Service.getInstance();

        eventBus.addHandler(SelectElementEvent.TYPE,
                event -> getView().setElement(event.getElementDao()));
        eventBus.addHandler(UpdateStructureEvent.TYPE,
                event -> loadCompoundInfo(event.getStructureDao()));
        eventBus.addHandler(BuildCompoundEvent.TYPE,
                event -> handleSmilesParse(event.getCompoundDao().getSmiles()));
    }

    @Override
    public void handleElementClick(String sign) {
        elementService.getElement(sign, new ErrorHandlerCallback<ElementDao>() {
            @Override
            public void onSuccess(ElementDao result) {
                getView().setElement(result);
            }
        });
    }

    @Override
    public void handleSmilesParse(String smiles) {
        compoundService.parseSmiles(smiles, new ErrorHandlerCallback<StructureDao>() {
            @Override
            public void onSuccess(StructureDao structure) {
                getView().setStructure(structure);
            }
        });
    }

    @Override
    public void handlePredictionClick(List<LinkDao> links) {
        predictionService.predict(links, new ErrorHandlerCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                getView().showPredictionData(result);
            }
        });
    }

    @Override
    public void handlePeriodicTableClick() {
        addToPopupSlot(new PeriodicTablePresenter(getEventBus(), periodicTableView));
    }

    @Override
    public void handleUpdateStructure(StructureDao structureDao) {
        loadCompoundInfo(structureDao);
    }

    @Override
    public void handleCompoundsClick() {
        addToPopupSlot(new CompoundsPresenter(getEventBus(), compoundsView));
    }

    @Override
    public void handleReportClick() {
        addToPopupSlot(new ReportPresenter(getEventBus(), reportView));
    }

    @Override
    public void handleTrainClick() {
        addToPopupSlot(new TrainPresenter(getEventBus(), trainView));
    }

    public void loadCompoundInfo(StructureDao structureDao) {
        compoundService.getCompound(structureDao, new ErrorHandlerCallback<CompoundDao>() {
            @Override
            public void onSuccess(CompoundDao result) {
                getView().showCompoundInfo(result);
            }
        });
    }
}
