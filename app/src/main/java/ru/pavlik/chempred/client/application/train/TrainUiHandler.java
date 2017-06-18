package ru.pavlik.chempred.client.application.train;

import com.gwtplatform.mvp.client.UiHandlers;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.TrainMethod;

import java.util.List;

public interface TrainUiHandler extends UiHandlers {

    void loadCompounds();

    void loadSourceDescriptors();

    void searchCompound(String query);

    void loadCompoundDescriptors(CompoundDao compoundDao);

    void handlerLELTrain(List<CompoundDao> compounds, TrainMethod trainMethod);

    void handlerUELTrain(List<CompoundDao> compounds, TrainMethod trainMethod);
}
