package ru.pavlik.chempred.client.services.descriptor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.DescriptorType;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("descriptor")
public interface DescriptorService extends RemoteService {

    List<String> getSourceDescriptors(DescriptorType descriptorType);

    Map<DescriptorType, List<String>> getSourceDescriptors();

    Map<String, Integer> getCompoundDescriptors(CompoundDao compound);

    class Service {
        private static DescriptorServiceAsync descriptorService = GWT.create(DescriptorService.class);

        public static synchronized DescriptorServiceAsync getInstance() {
            return descriptorService;
        }
    }
}
