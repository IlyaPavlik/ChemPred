package ru.pavlik.chempred.client.application.compounds;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import org.gwtbootstrap3.client.ui.*;
import ru.pavlik.chempred.client.model.dao.CompoundDao;

import javax.inject.Inject;
import java.util.List;

public class CompoundsView extends PopupViewWithUiHandlers<CompoundsUiHandler> implements CompoundsPresenter.MyView {

    @UiField
    LinkedGroup linkedGroup;
    @UiField
    Lead title;
    @UiField
    Description bruttoContainer;
    @UiField
    DescriptionData brutto;
    @UiField
    Description smilesContainer;
    @UiField
    DescriptionData smiles;
    @UiField
    Heading placeholder;
    @UiField
    Button build;
    @UiField
    TextBox search;

    private HandlerRegistration keyHandler;

    interface Binder extends UiBinder<PopupPanel, CompoundsView> {
    }

    @Inject
    public CompoundsView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        keyHandler = asWidget().getParent().addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                hide();
            }
        }, KeyDownEvent.getType());

        search.addKeyUpHandler(event -> getUiHandlers().searchCompound(search.getValue()));

        getUiHandlers().loadCompounds();
    }

    @Override
    protected void onDetach() {
        keyHandler.removeHandler();
        visiblePlaceholder(true);
        search.setText("");
    }

    @Override
    public void showCompounds(List<CompoundDao> compounds) {
        linkedGroup.clear();
        for (CompoundDao compound : compounds) {
            LinkedGroupItem listGroupItem = new LinkedGroupItem();
            listGroupItem.setText(compound.getName());
            listGroupItem.addDomHandler(event -> showCompoundInfo(compound), ClickEvent.getType());
            linkedGroup.add(listGroupItem);
        }
    }

    private void showCompoundInfo(CompoundDao compoundDao) {
        visiblePlaceholder(false);

        title.setText(compoundDao.getName());
        brutto.setText(compoundDao.getBrutto());
        smiles.setText(compoundDao.getSmiles());
        build.addDomHandler(event -> {
            getUiHandlers().handleBuildCompound(compoundDao);
            hide();
        }, ClickEvent.getType());
    }

    private void visiblePlaceholder(boolean visible) {
        placeholder.setVisible(visible);
        title.setVisible(!visible);
        bruttoContainer.setVisible(!visible);
        smilesContainer.setVisible(!visible);
        build.setVisible(!visible);
    }
}
