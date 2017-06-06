package ru.pavlik.chempred.client.application.home;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import ru.pavlik.chempred.client.model.LinkType;
import ru.pavlik.chempred.client.model.converter.ElementConverter;
import ru.pavlik.chempred.client.model.converter.StructureConverter;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.model.js.Structure;
import ru.pavlik.chempred.client.widgets.DrawPanelWidget;
import ru.pavlik.chempred.client.widgets.compoundinfo.CompoundInfoWidget;
import ru.pavlik.chempred.client.widgets.prediction.PredictionWidget;

import javax.inject.Inject;

public class HomeView extends ViewWithUiHandlers<PresenterUiHandler> implements HomePresenter.MyView {

    @UiField
    DrawPanelWidget drawPanel;
    @UiField
    TextBox smilesField;
    @UiField
    CompoundInfoWidget compoundInfo;
    @UiField
    PredictionWidget predictionInfo;
    @UiField
    Button infoButton;
    @UiField
    Collapse bottomCollapse;

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

        Scheduler.get().scheduleDeferred(() -> {
            drawPanel.init(drawPanel.getOffsetWidth(), drawPanel.getOffsetHeight());
        });

        drawPanel.setOnStructureUpdateListener(structure -> {
            StructureDao structureDao = structureConverter.convertToDao(structure);
            getUiHandlers().handleUpdateStructure(structureDao);
        });
        predictionInfo.setOnPredictClickListener(() -> {
            Structure structure = drawPanel.getStructure();
            StructureDao structureDao = structureConverter.convertToDao(structure);
            getUiHandlers().handlePredictionClick(structureDao.getLinks());
        });

        infoButton.addClickHandler(event -> {
            if (bottomCollapse.isHidden()) {
                infoButton.setIcon(IconType.CHEVRON_DOWN);
            } else {
                infoButton.setIcon(IconType.CHEVRON_UP);
            }
        });
    }

    @Override
    public void setElement(ElementDao element) {
        drawPanel.setCurrentElement(elementConverter.convertToNative(element));
    }

    @Override
    public void setStructure(StructureDao structureDao) {
        Structure structure = structureConverter.convertToNative(structureDao.getElements(), structureDao.getLinks());
        drawPanel.setStructure(structure);
    }

    @Override
    public void showCompoundInfo(CompoundDao compoundDao) {
        compoundInfo.setCompoundName(compoundDao.getName());
        compoundInfo.setBrutto(compoundDao.getBrutto());
        compoundInfo.setSmiles(compoundDao.getSmiles());
    }

    @Override
    public void showPredictionData(double lowRatio) {
        NumberFormat numberFormat = NumberFormat.getFormat("#.##");
        predictionInfo.setLowRatio(numberFormat.format(lowRatio));
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
        getUiHandlers().handlePeriodicTableClick();
    }

    @UiHandler("linkOne")
    public void onLinkOneClick(ClickEvent clickEvent) {
        drawPanel.setLinkType(LinkType.SINGLE);
    }

    @UiHandler("linkTwo")
    public void onLinkTwoClick(ClickEvent clickEvent) {
        drawPanel.setLinkType(LinkType.DOUBLE);
    }

    @UiHandler("linkThree")
    public void onLinkThreeClick(ClickEvent clickEvent) {
        drawPanel.setLinkType(LinkType.TRIPLE);
    }

    @UiHandler("linkTop")
    public void onLinkTopClick(ClickEvent clickEvent) {
        drawPanel.setLinkType(LinkType.TOP);
    }

    @UiHandler("linkDown")
    public void onLinkDownClick(ClickEvent clickEvent) {
        drawPanel.setLinkType(LinkType.DOWN);
    }

    @UiHandler("clear")
    public void onClearClick(ClickEvent clickEvent) {
        drawPanel.clear();
        compoundInfo.clear();
        predictionInfo.clear();
    }

    @UiHandler("smilesBuild")
    public void onBuildSmilesClick(ClickEvent clickEvent) {
        getUiHandlers().handleSmilesParse(smilesField.getText());
    }

    @UiHandler("compounds")
    public void onCompoundsClick(ClickEvent clickEvent) {
        getUiHandlers().handleCompoundsClick();
    }

    @UiHandler("train")
    public void onTrainClick(ClickEvent clickEvent) {
        getUiHandlers().handleTrainClick();
    }

    @UiHandler("report")
    public void onReportClick(ClickEvent clickEvent) {
        getUiHandlers().handleReportClick();
    }
}
