package ru.pavlik.chempred.client.model.dao;

import java.io.Serializable;
import java.util.List;

public class StructureDao implements Serializable {

    private List<ElementDao> elements;
    private List<LinkDao> links;

    public List<ElementDao> getElements() {
        return elements;
    }

    public void setElements(List<ElementDao> elements) {
        this.elements = elements;
    }

    public List<LinkDao> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDao> links) {
        this.links = links;
    }
}
