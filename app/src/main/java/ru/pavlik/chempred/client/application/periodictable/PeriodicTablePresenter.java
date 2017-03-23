package ru.pavlik.chempred.client.application.periodictable;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.events.SelectElementEvent;
import ru.pavlik.chempred.client.services.element.ElementService;
import ru.pavlik.chempred.client.services.element.ElementServiceAsync;
import ru.pavlik.chempred.client.utils.Utils;

import java.util.List;

public class PeriodicTablePresenter extends PresenterWidget<PeriodicTablePresenter.MyView> implements PresenterUiHandler {

    private ElementServiceAsync elementService;

    interface MyView extends PopupView, HasUiHandlers<PresenterUiHandler> {
        void showElements(List<ElementDao> elements);
    }

    public PeriodicTablePresenter(
            EventBus eventBus,
            MyView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
        elementService = ElementService.Service.getInstance();
    }

    public void loadElements() {
        elementService.getElements(new AsyncCallback<List<ElementDao>>() {
            @Override
            public void onFailure(Throwable caught) {
                Utils.console(caught);
            }

            @Override
            public void onSuccess(List<ElementDao> result) {
                Utils.console("Elements loaded: " + result.size());
                getView().showElements(result);
            }
        });
    }

    @Override
    public void handleElementClick(ElementDao elementDao) {
        getEventBus().fireEvent(new SelectElementEvent(elementDao));
    }
}
