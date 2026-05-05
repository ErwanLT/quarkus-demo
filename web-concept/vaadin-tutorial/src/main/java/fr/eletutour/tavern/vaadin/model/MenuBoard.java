package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record MenuBoard(
        String title,
        String chefNote,
        List<MenuSection> sections) {
}
