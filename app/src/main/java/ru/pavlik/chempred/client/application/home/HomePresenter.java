package ru.pavlik.chempred.client.application.home;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import ru.pavlik.chempred.client.application.periodictable.PeriodicTablePresenter;
import ru.pavlik.chempred.client.application.periodictable.PeriodicTableView;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.model.events.SelectElementEvent;
import ru.pavlik.chempred.client.model.rpc.ErrorHandlerCallback;
import ru.pavlik.chempred.client.place.NameTokens;
import ru.pavlik.chempred.client.services.element.ElementService;
import ru.pavlik.chempred.client.services.element.ElementServiceAsync;
import ru.pavlik.chempred.client.services.prediction.PredictionService;
import ru.pavlik.chempred.client.services.prediction.PredictionServiceAsync;
import ru.pavlik.chempred.client.services.smiles.SmilesService;
import ru.pavlik.chempred.client.services.smiles.SmilesServiceAsync;
import ru.pavlik.chempred.client.utils.Utils;

import javax.inject.Inject;
import java.util.List;

public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy> implements PresenterUiHandler {

    private ElementServiceAsync elementService;
    private SmilesServiceAsync smilesService;
    private PredictionServiceAsync predictionService;

    @Inject
    PeriodicTableView periodicTableView;

    interface MyView extends View, HasUiHandlers<PresenterUiHandler> {
        void setElement(ElementDao element);

        void setStructure(StructureDao structure);
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
        smilesService = SmilesService.Service.getInstance();
        predictionService = PredictionService.App.getInstance();

        eventBus.addHandler(SelectElementEvent.TYPE, event -> {
            getView().setElement(event.getElementDao());
        });
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
        smilesService.parseSmiles(smiles, new ErrorHandlerCallback<StructureDao>() {
            @Override
            public void onSuccess(StructureDao structure) {
                getView().setStructure(structure);
            }
        });
    }

    @Override
    public void handleTrainClick() {
        predictionService.train(new ErrorHandlerCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                Utils.console("Total error: " + result);
            }
        });
    }

    @Override
    public void handlePredictionClick(List<LinkDao> links) {
        predictionService.predict(links, new ErrorHandlerCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                Utils.console(result);
            }
        });
    }

    @Override
    public void handlePeriodicTableClick() {
        addToPopupSlot(new PeriodicTablePresenter(getEventBus(), periodicTableView));
    }
}
