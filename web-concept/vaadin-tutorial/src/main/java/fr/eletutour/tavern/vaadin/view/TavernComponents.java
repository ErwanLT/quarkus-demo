package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.eletutour.tavern.vaadin.model.HighlightMetric;

import java.util.List;

final class TavernComponents {

    private TavernComponents() {
    }

    static VerticalLayout createPageLayout(String eyebrow, String title, String description) {
        Span kicker = new Span(eyebrow);
        kicker.setClassName("page-kicker");

        H1 heading = new H1(title);
        heading.setClassName("page-title");

        Paragraph text = new Paragraph(description);
        text.setClassName("page-description");

        VerticalLayout header = new VerticalLayout(kicker, heading, text);
        header.setPadding(false);
        header.setSpacing(false);
        header.setClassName("page-hero");
        return header;
    }

    static Div createSection(String title, Component... content) {
        H2 heading = new H2(title);
        heading.setClassName("section-title");

        Div section = new Div();
        section.setWidthFull();
        section.setClassName("section-block");
        section.add(heading);
        for (Component component : content) {
            section.add(component);
        }
        return section;
    }

    static HorizontalLayout createMetricGrid(List<HighlightMetric> metrics) {
        HorizontalLayout grid = new HorizontalLayout();
        grid.setWidthFull();
        grid.setSpacing(true);
        grid.setPadding(false);
        
        metrics.stream()
                .map(TavernComponents::createMetricCard)
                .forEach(card -> {
                    grid.add(card);
                    grid.setFlexGrow(1, card);
                });
        return grid;
    }

    static Div createMetricCard(HighlightMetric metric) {
        Span label = new Span(metric.label());
        label.setClassName("metric-label");

        H3 value = new H3(metric.value());
        value.setClassName("metric-value");

        Paragraph detail = new Paragraph(metric.detail());
        detail.setClassName("metric-detail");

        Div card = new Div(label, value, detail);
        card.setClassName("metric-card");
        return card;
    }

    static Div createPanel(String title, String body) {
        H3 heading = new H3(title);
        heading.setClassName("panel-title");

        Paragraph text = new Paragraph(body);
        text.setClassName("panel-copy");

        Div panel = new Div(heading, text);
        panel.setClassName("whale-panel");
        return panel;
    }

    static Div createListPanel(String title, List<String> items) {
        H3 heading = new H3(title);
        heading.setClassName("panel-title");

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);
        list.setClassName("stack-list");
        items.stream().map(TavernComponents::createListItem).forEach(list::add);

        Div panel = new Div(heading, list);
        panel.setClassName("whale-panel");
        return panel;
    }

    private static Paragraph createListItem(String item) {
        Paragraph row = new Paragraph(item);
        row.setClassName("stack-item");
        return row;
    }

    private static void applySurface(Div surface) {
        surface.setClassName("whale-panel");
    }
}
