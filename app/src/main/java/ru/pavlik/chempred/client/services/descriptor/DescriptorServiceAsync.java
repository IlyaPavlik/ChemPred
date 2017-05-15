package ru.pavlik.chempred.client.services.descriptor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.DescriptorType;

import java.util.List;
import java.util.Map;

public interface DescriptorServiceAsync {
    void getSourceDescriptors(DescriptorType descriptorType, AsyncCallback<List<String>> async);

    void getCompoundDescriptors(CompoundDao compound, AsyncCallback<Map<String, Integer>> async);

    void getSourceDescriptors(AsyncCallback<Map<DescriptorType, List<String>>> async);
}
