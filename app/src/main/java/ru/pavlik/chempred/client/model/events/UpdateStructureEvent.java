package ru.pavlik.chempred.client.model.events;

import com.google.web.bindery.event.shared.Event;
import ru.pavlik.chempred.client.model.dao.StructureDao;

public class UpdateStructureEvent extends Event<UpdateStructureEvent.UpdateStructureHandler> {

    public static Type<UpdateStructureEvent.UpdateStructureHandler> TYPE = new Type<>();

    private StructureDao structureDao;

    public UpdateStructureEvent(StructureDao structureDao) {
        this.structureDao = structureDao;
    }

    public StructureDao getStructureDao() {
        return structureDao;
    }

    @Override
    public Type<UpdateStructureHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UpdateStructureHandler handler) {
        handler.onUpdate(this);
    }

    public interface UpdateStructureHandler {
        void onUpdate(UpdateStructureEvent event);
    }
}
