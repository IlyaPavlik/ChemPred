package ru.pavlik.chempred.client.application.compounds;

import com.gwtplatform.mvp.client.UiHandlers;
import ru.pavlik.chempred.client.model.dao.CompoundDao;

public interface CompoundsUiHandler extends UiHandlers {

    void loadCompounds();

    void handleBuildCompound(CompoundDao compoundDao);

    void searchCompound(String query);

}
