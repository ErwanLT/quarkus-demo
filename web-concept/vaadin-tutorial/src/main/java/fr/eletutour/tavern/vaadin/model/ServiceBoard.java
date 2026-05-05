package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record ServiceBoard(
        List<HighlightMetric> serviceMetrics,
        List<String> teamAssignments,
        String shiftReadingTitle,
        String shiftReading,
        String drawerAlertTitle,
        String drawerAlertDescription) {
}
