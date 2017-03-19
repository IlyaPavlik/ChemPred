package ru.pavlik.chempred.client.model.dao;

import java.io.Serializable;

public class ElementDao implements Serializable {

    private Integer id;
    private String symbol;
    private String name;
    private Integer period;
    private Double weight;
    private Integer group;
    private Integer valence;
    private Double electronegativity;

    public ElementDao() {
    }

    public ElementDao(ElementDao elementDao) {
        setId(elementDao.getId());
        setSymbol(elementDao.getSymbol());
        setName(elementDao.getName());
        setPeriod(elementDao.getPeriod());
        setWeight(elementDao.getWeight());
        setGroup(elementDao.getGroup());
        setValence(elementDao.getValence());
        setElectronegativity(elementDao.getElectronegativity());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getValence() {
        return valence;
    }

    public void setValence(Integer valence) {
        this.valence = valence;
    }

    public Double getElectronegativity() {
        return electronegativity;
    }

    public void setElectronegativity(Double electronegativity) {
        this.electronegativity = electronegativity;
    }
}
