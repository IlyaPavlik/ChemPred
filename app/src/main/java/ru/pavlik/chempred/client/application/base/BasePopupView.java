package ru.pavlik.chempred.client.application.base;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;

public abstract class BasePopupView<H extends UiHandlers> extends PopupViewWithUiHandlers<H> {

    private HandlerRegistration keyHandler;

    public BasePopupView(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected void onAttach() {
        keyHandler = asWidget().getParent().addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                hide();
            }
        }, KeyDownEvent.getType());
    }

    @Override
    protected void onDetach() {
        keyHandler.removeHandler();
    }
}
