package ru.pavlik.chempred.server.model.converter;

import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.server.model.Element;

public class ElementConverter extends BaseConverter<ElementDao, Element> {

    @Override
    public ElementDao convertToDao(Element element) {
        ElementDao elementDao = new ElementDao();
        elementDao.setId(element.getId());
        elementDao.setName(element.getName());
        elementDao.setGroup(element.getGroup());
        elementDao.setPeriod(element.getPeriod());
        elementDao.setSymbol(element.getSymbol());
        elementDao.setWeight(element.getWeight());
        elementDao.setElectronegativity(element.getElectronegativity());
        elementDao.setValence(element.getValence());
        return elementDao;
    }

    @Override
    public Element convertToDB(ElementDao elementDao) {
        return null;
    }

}
