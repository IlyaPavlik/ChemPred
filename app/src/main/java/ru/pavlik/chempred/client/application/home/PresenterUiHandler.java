package ru.pavlik.chempred.client.application.home;

import com.gwtplatform.mvp.client.UiHandlers;
import ru.pavlik.chempred.client.model.dao.StructureDao;

interface PresenterUiHandler extends UiHandlers {
    void handleElementClick(String sign);

    void handleSmilesParse(String smiles);

    void handleTrainClick();

    void handlePredictionClick(StructureDao structureDao);

    void handlePeriodicTableClick();

    void handleUpdateStructure(StructureDao structureDao);

    void handleCompoundsClick();

    void handleReportClick();

    void handleAddCompound(StructureDao structureDao);
}
