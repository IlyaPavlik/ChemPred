package ru.pavlik.chempred.client.services.smiles;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

public interface SmilesServiceAsync {
    void parseStructure(List<LinkDao> links, AsyncCallback<String> async);

    void parseSmiles(String smiles, AsyncCallback<StructureDao> async);
}
