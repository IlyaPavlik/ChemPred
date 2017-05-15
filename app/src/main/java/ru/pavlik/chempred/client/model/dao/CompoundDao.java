package ru.pavlik.chempred.client.model.dao;

import java.io.Serializable;

public class CompoundDao implements Serializable {

    private int id;
    private String name;
    private String brutto;
    private String smiles;
    private double lowFactor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrutto() {
        return brutto;
    }

    public void setBrutto(String brutto) {
        this.brutto = brutto;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public double getLowFactor() {
        return lowFactor;
    }

    public void setLowFactor(double lowFactor) {
        this.lowFactor = lowFactor;
    }
}
