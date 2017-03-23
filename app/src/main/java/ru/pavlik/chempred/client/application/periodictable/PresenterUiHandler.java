package ru.pavlik.chempred.client.application.periodictable;

import com.gwtplatform.mvp.client.UiHandlers;
import ru.pavlik.chempred.client.model.dao.ElementDao;

interface PresenterUiHandler extends UiHandlers {
    void loadElements();

    void handleElementClick(ElementDao elementDao);
}
