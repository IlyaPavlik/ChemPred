package client.pages.main;

import client.base.BasePresenter;
import client.base.BaseView;
import com.google.gwt.user.client.ui.HasWidgets;

public class MainBasePresenter implements BasePresenter {

    interface View extends BaseView {

    }

    private View view;

    public void go(HasWidgets container) {
        view = new MainView(container);
        view.render();
    }
}
