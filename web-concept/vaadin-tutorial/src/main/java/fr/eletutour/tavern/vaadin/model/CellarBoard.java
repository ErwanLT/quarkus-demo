package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record CellarBoard(
        List<CellarStock> stocks,
        String cellarNoteTitle,
        String cellarNote,
        List<String> cellarTasks) {
}
