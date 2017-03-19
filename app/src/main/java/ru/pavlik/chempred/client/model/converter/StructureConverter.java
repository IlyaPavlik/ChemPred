package ru.pavlik.chempred.client.model.converter;

import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.js.ElementLink;
import ru.pavlik.chempred.client.model.js.ElementNode;
import ru.pavlik.chempred.client.model.js.Structure;
import ru.pavlik.chempred.client.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureConverter {

    private ElementConverter elementConverter = new ElementConverter();

    public Structure convertToNative(List<ElementDao> daoElements, List<LinkDao> daoLinks) {
        List<ElementLink> elementLinks = new ArrayList<>();
        Map<ElementDao, ElementNode> elementNodes = new HashMap<>();

        for (ElementDao daoElement : daoElements) {
            elementNodes.put(daoElement, elementConverter.convertToNative(daoElement));
            Utils.console(daoElement.hashCode());
        }

        for (LinkDao daoLink : daoLinks) {
            elementLinks.add(
                    ElementLink.create(
                            elementNodes.get(daoLink.getElementSource()),
                            elementNodes.get(daoLink.getElementTarget()),
                            daoLink.getLinkType()
                    )
            );
        }


        Structure structure = new Structure();
        structure.setElementNodes(new ArrayList<>(elementNodes.values()));
        structure.setElementLinks(elementLinks);
        return structure;
    }
}
