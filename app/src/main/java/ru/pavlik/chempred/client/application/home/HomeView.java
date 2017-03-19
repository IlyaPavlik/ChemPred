package ru.pavlik.chempred.client.application.home;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import org.gwtbootstrap3.client.ui.VerticalButtonGroup;
import ru.pavlik.chempred.client.model.LinkType;
import ru.pavlik.chempred.client.model.converter.ElementConverter;
import ru.pavlik.chempred.client.model.converter.StructureConverter;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.model.js.Structure;
import ru.pavlik.chempred.client.widgets.DrawPanelWidget;

import javax.inject.Inject;

public class HomeView extends ViewWithUiHandlers<PresenterUiHandler> implements HomePresenter.MyView {

    @UiField
    DrawPanelWidget panel;
    @UiField
    VerticalButtonGroup menuLeft;

    private ElementConverter elementConverter = new ElementConverter();
    private StructureConverter structureConverter = new StructureConverter();

    interface Binder extends UiBinder<Widget, HomeView> {
    }

    @Inject
    HomeView(Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        Scheduler.get().scheduleDeferred(() ->
                panel.init(panel.getOffsetWidth(), panel.getOffsetHeight()));
    }

    @Override
    public void setElement(ElementDao element) {
        panel.setCurrentElement(elementConverter.convertToNative(element));
    }

    @Override
    public void setStructure(StructureDao structureDao) {
        Structure structure = structureConverter.convertToNative(structureDao.getElements(), structureDao.getLinks());
        panel.setStructure(structure);
    }

    @UiHandler("elementC")
    public void onCClick(ClickEvent clickEvent) {
        getUiHandlers().handleElementClick("C");
    }

    @UiHandler("elementO")
    public void onOClick(ClickEvent clickEvent) {
        getUiHandlers().handleElementClick("O");
    }

    @UiHandler("elementP")
    public void onPClick(ClickEvent clickEvent) {
        getUiHandlers().handleElementClick("P");
    }

    @UiHandler("elementS")
    public void onSClick(ClickEvent clickEvent) {
        getUiHandlers().handleElementClick("S");
    }

    @UiHandler("elementN")
    public void onNClick(ClickEvent clickEvent) {
        getUiHandlers().handleElementClick("N");
    }

    @UiHandler("elementCl")
    public void onClClick(ClickEvent clickEvent) {
        getUiHandlers().handleElementClick("Cl");
    }

    @UiHandler("periodicTable")
    public void onPeriodicTableClick(ClickEvent clickEvent) {
        // getUiHandlers().handleSmilesParse("CNC");
    }

    @UiHandler("linkOne")
    public void onLinkOneClick(ClickEvent clickEvent) {
        panel.setLinkType(LinkType.SINGLE);
    }

    @UiHandler("linkTwo")
    public void onLinkTwoClick(ClickEvent clickEvent) {
        panel.setLinkType(LinkType.DOUBLE);
    }

    @UiHandler("linkThree")
    public void onLinkThreeClick(ClickEvent clickEvent) {
        panel.setLinkType(LinkType.TRIPLE);
    }

    @UiHandler("linkTop")
    public void onLinkTopClick(ClickEvent clickEvent) {
        panel.setLinkType(LinkType.TOP);
    }

    @UiHandler("linkDown")
    public void onLinkDownClick(ClickEvent clickEvent) {
        panel.setLinkType(LinkType.DOWN);
    }

    @UiHandler("clear")
    public void onClearClick(ClickEvent clickEvent) {
        panel.clear();
    }
}
