package client.pages.main;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;

public class MainView extends Composite implements MainBasePresenter.View {

    private HasWidgets rootContainer;

    public MainView(HasWidgets container) {
        rootContainer = container;
    }

    @Override
    public void render() {
        rootContainer.clear();

        DockLayoutPanel p = new DockLayoutPanel(Style.Unit.EM);
        p.addNorth(new HTML("header"), 5);
        p.addSouth(new HTML("footer"), 5);
        p.addWest(new HTML("navigation"), 5);
        p.addEast(new HTML("navigation"), 5);

        Canvas canvas = Canvas.createIfSupported();
        if (canvas != null) {
            canvas.setStyleName("main-canvas");
            p.add(canvas);
        }
        rootContainer.add(p);
    }
}
