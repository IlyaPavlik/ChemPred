package client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class ChemPredApp implements EntryPoint {

    public void onModuleLoad() {
        ChemPredAppServiceAsync serviceAsync = GWT.create(ChemPredAppService.class);
        HandlerManager eventBus = new HandlerManager(null);
        ChemPredController controller = new ChemPredController(serviceAsync, eventBus);
        controller.go(RootLayoutPanel.get());
    }
}
