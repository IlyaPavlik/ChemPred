package ru.pavlik.chempred.client.services.compound;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

@RemoteServiceRelativePath("compound")
public interface CompoundService extends RemoteService {

    StructureDao parseSmiles(String smiles);

    String parseStructure(List<LinkDao> links);

    CompoundDao getCompound(StructureDao structureDao);

    class Service {
        private static CompoundServiceAsync ourInstance = GWT.create(CompoundService.class);

        public static synchronized CompoundServiceAsync getInstance() {
            return ourInstance;
        }
    }

}
