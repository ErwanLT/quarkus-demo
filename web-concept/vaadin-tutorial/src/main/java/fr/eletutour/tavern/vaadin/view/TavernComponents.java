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
import fr.eletutour.tavern.vaadin.view.component.MetricCard;
import fr.eletutour.tavern.vaadin.view.component.TavernPanel;

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
                .map(MetricCard::new)
                .forEach(card -> {
                    grid.add(card);
                    grid.setFlexGrow(1, card);
                });
        return grid;
    }

    static Div createPanel(String title, String body) {
        return new TavernPanel(title, body);
    }

    static Div createListPanel(String title, List<String> items) {
        return new TavernPanel(title, items);
    }

    private static void applySurface(Div surface) {
        surface.setClassName("whale-panel");
    }
}
