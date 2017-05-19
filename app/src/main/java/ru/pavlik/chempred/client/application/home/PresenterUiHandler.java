package ru.pavlik.chempred.client.application.home;

import com.gwtplatform.mvp.client.UiHandlers;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.List;

interface PresenterUiHandler extends UiHandlers {
    void handleElementClick(String sign);

    void handleSmilesParse(String smiles);

    void handleTrainClick();

    void handlePredictionClick(List<LinkDao> links);

    void handlePeriodicTableClick();

    void handleUpdateStructure(StructureDao structureDao);

    void handleCompoundsClick();

    void handleReportClick();
}
