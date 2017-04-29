package ru.pavlik.chempred.client.application.compounds;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.events.BuildCompoundEvent;
import ru.pavlik.chempred.client.model.rpc.ErrorHandlerCallback;
import ru.pavlik.chempred.client.services.compound.CompoundService;
import ru.pavlik.chempred.client.services.compound.CompoundServiceAsync;

import java.util.List;

public class CompoundsPresenter extends PresenterWidget<CompoundsPresenter.MyView> implements CompoundsUiHandler {

    private CompoundServiceAsync compoundService;

    interface MyView extends PopupView, HasUiHandlers<CompoundsUiHandler> {

        void showCompounds(List<CompoundDao> compounds);
    }

    public CompoundsPresenter(EventBus eventBus, MyView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
        compoundService = CompoundService.Service.getInstance();
    }

    @Override
    public void loadCompounds() {
        compoundService.getCompounds(new ErrorHandlerCallback<List<CompoundDao>>() {
            @Override
            public void onSuccess(List<CompoundDao> result) {
                getView().showCompounds(result);
            }
        });
    }

    @Override
    public void handleBuildCompound(CompoundDao compoundDao) {
        getEventBus().fireEvent(new BuildCompoundEvent(compoundDao));
    }

    @Override
    public void searchCompound(String query) {
        compoundService.findCompounds(query, new ErrorHandlerCallback<List<CompoundDao>>() {
            @Override
            public void onSuccess(List<CompoundDao> result) {
                getView().showCompounds(result);
            }
        });
    }

}
