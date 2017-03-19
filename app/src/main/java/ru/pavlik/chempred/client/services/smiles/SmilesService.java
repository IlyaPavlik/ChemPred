package ru.pavlik.chempred.client.services.smiles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

@RemoteServiceRelativePath("smiles")
public interface SmilesService extends RemoteService {

    StructureDao parseSmiles(String smiles);

    String parseStructure(List<LinkDao> links);

    class Service {
        private static SmilesServiceAsync ourInstance = GWT.create(SmilesService.class);

        public static synchronized SmilesServiceAsync getInstance() {
            return ourInstance;
        }
    }

}
