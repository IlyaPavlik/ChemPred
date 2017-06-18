package ru.pavlik.chempred.client.model.dao;

public enum TrainMethod {

    BACK_PROPAGATION("Метод обратного распространения ошибки"),
    MOMENTUM_BACK_PROPAGATION("Метод градиентного спуска"),
    R_PROP("Метод rProp");

    private String name;

    TrainMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
