package ru.pavlik.chempred.client.model.dao;

public enum DescriptorType {

    ATOM("Атомы"),
    FRAGMENT("Фрагменты"),
    ATOM_ATOM("Атом-Атом"),
    FRAGMENT_FRAGMENT("Фрагмент-Фрагмент");

    private String title;

    DescriptorType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
