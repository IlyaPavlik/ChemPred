package ru.pavlik.chempred.client.application.periodictable;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.gwtbootstrap3.client.ui.Button;
import ru.pavlik.chempred.client.application.base.BasePopupView;
import ru.pavlik.chempred.client.model.dao.ElementDao;

import javax.inject.Inject;
import java.util.List;

public class PeriodicTableView extends BasePopupView<PresenterUiHandler> implements PeriodicTablePresenter.MyView {

    private static final int COLUMN_COUNT = 18;
    private static final int ROW_COUNT = 7 + 2; // +2 for extra elements

    interface Style extends CssResource {

        String popup();

        String popup_background();

        String periodic_table();

        String cell();

        String popupContent();
    }

    @UiField
    Grid grid;
    @UiField
    Style style;

    interface Binder extends UiBinder<PopupPanel, PeriodicTableView> {
    }

    @Inject
    public PeriodicTableView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
        grid.resize(ROW_COUNT, COLUMN_COUNT);

        Element contentWidget = grid.getElement().getParentElement();
        contentWidget.addClassName(style.popupContent());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        getUiHandlers().loadElements();
    }

    @Override
    public void showElements(List<ElementDao> elements) {
        for (ElementDao element : elements) {
            if (element.getGroup() != null && element.getPeriod() != null) {
                Button button = new Button();
                button.setText(element.getSymbol());
                button.addStyleName(style.cell());
                button.addClickHandler(event -> {
                    getUiHandlers().handleElementClick(element);
                    hide();
                });

                grid.setWidget(element.getPeriod() - 1, element.getGroup() - 1, button);
            }
        }
    }

}