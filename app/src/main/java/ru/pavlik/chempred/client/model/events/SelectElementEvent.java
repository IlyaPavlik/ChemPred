package ru.pavlik.chempred.client.model.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;
import ru.pavlik.chempred.client.model.dao.ElementDao;

public class SelectElementEvent extends Event<SelectElementEvent.SelectElementHandler> {

    public static Type<SelectElementEvent.SelectElementHandler> TYPE = new Type<>();

    private ElementDao elementDao;

    public SelectElementEvent(ElementDao elementDao) {
        this.elementDao = elementDao;
    }

    public ElementDao getElementDao() {
        return elementDao;
    }

    @Override
    public Type<SelectElementHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SelectElementHandler handler) {
        handler.onSelect(this);
    }

    public interface SelectElementHandler extends EventHandler {
        void onSelect(SelectElementEvent event);
    }

}
