package ru.pavlik.chempred.client.model;

public enum LinkType {
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    TOP(1),
    DOWN(1);

    private int weight;

    LinkType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
