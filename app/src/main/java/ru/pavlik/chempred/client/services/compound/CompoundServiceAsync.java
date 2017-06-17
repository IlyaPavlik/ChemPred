package ru.pavlik.chempred.client.services.compound;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

public interface CompoundServiceAsync {
    void parseStructure(List<LinkDao> links, AsyncCallback<String> async);

    void parseSmiles(String smiles, AsyncCallback<StructureDao> async);

    void getCompound(StructureDao structureDao, AsyncCallback<CompoundDao> async);

    void getCompounds(AsyncCallback<List<CompoundDao>> async);

    void findCompounds(String query, AsyncCallback<List<CompoundDao>> async);

    void addNewCompound(CompoundDao newCompound, AsyncCallback<Void> async);
}
