package ru.pavlik.chempred.client.application.compounds.compound_edit;

import com.gwtplatform.mvp.client.UiHandlers;
import ru.pavlik.chempred.client.model.dao.CompoundDao;

public interface CompoundEditUiHandler extends UiHandlers {

    void loadSmiles();

    void handlePredictLEL(String smiles);

    void handlePredictUEL(String smiles);

    void handleAddCompound(CompoundDao newCompound);

}
