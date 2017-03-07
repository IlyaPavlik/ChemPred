package ru.pavlik.chempred.client.model.converter;

import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.js.ElementNode;

public class ElementConverter extends BaseConverter<ElementDao, ElementNode> {

    @Override
    public ElementDao convertToDao(ElementNode elementNode) {
        ElementDao elementDao = new ElementDao();
        //TODO implement binding
        return elementDao;
    }

    @Override
    public ElementNode convertToNative(ElementDao elementDao) {
        ElementNode elementNode = ElementNode.create();
        elementNode.setAtom(elementDao.getSymbol());
        elementNode.setValence(elementDao.getValence());
        return elementNode;
    }
}
