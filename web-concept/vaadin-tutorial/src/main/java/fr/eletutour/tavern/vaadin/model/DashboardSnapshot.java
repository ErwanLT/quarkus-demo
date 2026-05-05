package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record DashboardSnapshot(
        String heroTitle,
        String heroDescription,
        List<HighlightMetric> metrics,
        String roomHeadline,
        String roomStory,
        List<String> priorities,
        List<String> tonightHighlights) {
}
