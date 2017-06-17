package ru.pavlik.chempred.client.application.report;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ReportUiHandler extends UiHandlers {
    void loadData(boolean useLEL);
}
