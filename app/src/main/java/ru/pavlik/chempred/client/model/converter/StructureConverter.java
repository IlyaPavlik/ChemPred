package ru.pavlik.chempred.client.model.converter;

import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.model.js.ElementLink;
import ru.pavlik.chempred.client.model.js.ElementNode;
import ru.pavlik.chempred.client.model.js.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureConverter {

    private ElementConverter elementConverter = new ElementConverter();

    public StructureDao convertToDao(Structure structure) {
        List<LinkDao> daoLinks = new ArrayList<>();
        Map<ElementNode, ElementDao> daoNodes = new HashMap<>();

        for (ElementNode elementNode : structure.getElementNodes()) {
            daoNodes.put(elementNode, elementConverter.convertToDao(elementNode));
        }

        for (ElementLink daoLink : structure.getElementLinks()) {
            daoLinks.add(new LinkDao(
                    daoNodes.get(daoLink.source()),
                    daoNodes.get(daoLink.target()),
                    daoLink.getType()
            ));
        }

        StructureDao structureDao = new StructureDao();
        structureDao.setElements(new ArrayList<>(daoNodes.values()));
        structureDao.setLinks(daoLinks);
        return structureDao;
    }

    public Structure convertToNative(List<ElementDao> daoElements, List<LinkDao> daoLinks) {
        List<ElementLink> elementLinks = new ArrayList<>();
        Map<ElementDao, ElementNode> elementNodes = new HashMap<>();

        for (ElementDao daoElement : daoElements) {
            elementNodes.put(daoElement, elementConverter.convertToNative(daoElement));
        }

        for (LinkDao daoLink : daoLinks) {
            ElementNode source = elementNodes.get(daoLink.getElementSource());
            ElementNode target = elementNodes.get(daoLink.getElementTarget());

            source.setValence(source.getValence() - daoLink.getLinkType().getWeight());
            target.setValence(target.getValence() - daoLink.getLinkType().getWeight());

            elementLinks.add(ElementLink.create(source, target, daoLink.getLinkType()));
        }

        Structure structure = new Structure();
        structure.setElementNodes(new ArrayList<>(elementNodes.values()));
        structure.setElementLinks(elementLinks);
        return structure;
    }
}
