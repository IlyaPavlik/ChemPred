package ru.pavlik.chempred.client.model.js;

import java.util.List;

public class Structure {

    private List<ElementNode> elementNodes;
    private List<ElementLink> elementLinks;

    public List<ElementNode> getElementNodes() {
        return elementNodes;
    }

    public void setElementNodes(List<ElementNode> elementNodes) {
        this.elementNodes = elementNodes;
    }

    public List<ElementLink> getElementLinks() {
        return elementLinks;
    }

    public void setElementLinks(List<ElementLink> elementLinks) {
        this.elementLinks = elementLinks;
    }
}
