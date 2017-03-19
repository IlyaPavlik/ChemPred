package ru.pavlik.chempred.client.model.dao;

import ru.pavlik.chempred.client.model.LinkType;

import java.io.Serializable;

public class LinkDao implements Serializable {

    private ElementDao elementSource;
    private ElementDao elementTarget;
    private LinkType linkType;

    public LinkDao() {
    }

    public LinkDao(ElementDao elementSource, ElementDao elementTarget, LinkType linkType) {
        this.elementSource = elementSource;
        this.elementTarget = elementTarget;
        this.linkType = linkType;
    }

    public ElementDao getElementSource() {
        return elementSource;
    }

    public void setElementSource(ElementDao elementSource) {
        this.elementSource = elementSource;
    }

    public ElementDao getElementTarget() {
        return elementTarget;
    }

    public void setElementTarget(ElementDao elementTarget) {
        this.elementTarget = elementTarget;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }
}
