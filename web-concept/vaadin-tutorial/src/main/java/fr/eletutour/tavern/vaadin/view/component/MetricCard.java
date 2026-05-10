package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import fr.eletutour.tavern.vaadin.model.HighlightMetric;

/**
 * A custom component to display a performance metric card.
 */
public class MetricCard extends Div {

    public MetricCard(HighlightMetric metric) {
        setClassName("metric-card");

        Span label = new Span(metric.label());
        label.setClassName("metric-label");

        H3 value = new H3(metric.value());
        value.setClassName("metric-value");

        Paragraph detail = new Paragraph(metric.detail());
        detail.setClassName("metric-detail");

        add(label, value, detail);
    }
}
