package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record AnalyticsData(
        String title,
        List<String> labels,
        List<Double> values,
        String chartType // "bar", "pie", "line"
) {
}
