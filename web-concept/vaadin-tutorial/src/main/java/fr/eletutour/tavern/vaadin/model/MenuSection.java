package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record MenuSection(
        String title,
        String description,
        List<MenuEntry> entries) {
}
