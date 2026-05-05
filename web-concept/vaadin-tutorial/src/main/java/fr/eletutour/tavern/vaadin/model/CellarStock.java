package fr.eletutour.tavern.vaadin.model;

public record CellarStock(
        String productName,
        int currentLevel,
        int maxLevel,
        String unit,
        String note) {
}
