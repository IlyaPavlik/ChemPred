package ru.pavlik.chempred.client.model.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;
import ru.pavlik.chempred.client.model.dao.CompoundDao;

public class BuildCompoundEvent extends Event<BuildCompoundEvent.BuildCompoundHandler> {

    public static Type<BuildCompoundHandler> TYPE = new Type<>();

    private CompoundDao compoundDao;

    public BuildCompoundEvent(CompoundDao compoundDao) {
        this.compoundDao = compoundDao;
    }

    public CompoundDao getCompoundDao() {
        return compoundDao;
    }

    @Override
    public Type<BuildCompoundHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(BuildCompoundHandler handler) {
        handler.onBuild(this);
    }

    public interface BuildCompoundHandler extends EventHandler {
        void onBuild(BuildCompoundEvent event);
    }
}
