package client.pages.main;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;

public class MainView extends Composite implements MainPresenter.View {

    public MainView(HasWidgets container) {
        HTMLPanel htmlPanel = new HTMLPanel("h1", "Main View");
        container.add(htmlPanel);

        System.out.println("Element loaded!");
    }
}
