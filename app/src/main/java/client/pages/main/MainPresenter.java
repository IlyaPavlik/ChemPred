package client.pages.main;

import client.base.Presenter;
import com.google.gwt.user.client.ui.HasWidgets;

public class MainPresenter implements Presenter {

    interface View {

    }

    public void go(HasWidgets container) {
        new MainView(container);
    }
}
