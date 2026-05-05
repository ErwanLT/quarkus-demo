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
        kicker.addClassName("page-kicker");

        H1 heading = new H1(title);
        heading.addClassName("page-title");

        Paragraph text = new Paragraph(description);
        text.addClassName("page-description");

        VerticalLayout header = new VerticalLayout(kicker, heading, text);
        header.setPadding(false);
        header.setSpacing(false);
        header.addClassName("page-hero");
        header.getStyle()
                .set("gap", "0.2rem")
                .set("padding", "1rem 1.5rem")
                .set("background", "white")
                .set("border-bottom", "1px solid #dee2e6")
                .set("margin-bottom", "1rem");
        kicker.getStyle()
                .set("color", "#007bff")
                .set("font-size", "0.7rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em")
                .set("font-weight", "700");
        heading.getStyle()
                .set("margin", "0")
                .set("color", "#212529")
                .set("font-size", "1.75rem")
                .set("line-height", "1.2");
        text.getStyle()
                .set("margin", "0")
                .set("max-width", "50rem")
                .set("color", "#495057")
                .set("line-height", "1.4")
                .set("font-size", "0.95rem");
        return header;
    }

    static Div createSection(String title, Component... content) {
        H2 heading = new H2(title);
        heading.addClassName("section-title");

        Div section = new Div();
        section.setWidthFull();
        section.addClassName("section-block");
        section.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "0.75rem")
                .set("padding", "0 1.5rem")
                .set("margin-bottom", "2rem");
        heading.getStyle().set("margin", "0").set("color", "#212529").set("font-size", "1.25rem").set("font-weight", "600");
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
        H3 value = new H3(metric.value());
        Paragraph detail = new Paragraph(metric.detail());

        Div card = new Div(label, value, detail);
        card.setWidthFull();
        card.getStyle()
                .set("padding", "1rem")
                .set("background", "white")
                .set("border", "1px solid #dee2e6")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)")
                .set("display", "flex")
                .set("flex-direction", "column");

        label.getStyle()
                .set("color", "#6c757d")
                .set("font-size", "0.75rem")
                .set("text-transform", "uppercase")
                .set("font-weight", "700")
                .set("margin-bottom", "0.25rem");
        value.getStyle().set("margin", "0").set("color", "#007bff").set("font-size", "1.75rem");
        detail.getStyle().set("margin", "0.25rem 0 0").set("color", "#495057").set("font-size", "0.85rem");
        return card;
    }

    static Div createPanel(String title, String body) {
        H3 heading = new H3(title);
        heading.addClassName("panel-title");

        Paragraph text = new Paragraph(body);
        text.addClassName("panel-copy");

        Div panel = new Div(heading, text);
        panel.addClassName("panel-card");
        applySurface(panel);
        heading.getStyle().set("margin", "0").set("color", "#212529").set("font-size", "1.1rem").set("font-weight", "600");
        text.getStyle().set("margin", "0.5rem 0 0").set("color", "#495057").set("font-size", "0.95rem").set("line-height", "1.5");
        return panel;
    }

    static Div createListPanel(String title, List<String> items) {
        H3 heading = new H3(title);
        heading.addClassName("panel-title");

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);
        list.addClassName("stack-list");
        list.getStyle().set("margin-top", "0.75rem").set("gap", "0.5rem");
        items.stream().map(TavernComponents::createListItem).forEach(list::add);

        Div panel = new Div(heading, list);
        panel.addClassName("panel-card");
        applySurface(panel);
        heading.getStyle().set("margin", "0").set("color", "#212529").set("font-size", "1.1rem").set("font-weight", "600");
        return panel;
    }

    private static Paragraph createListItem(String item) {
        Paragraph row = new Paragraph(item);
        row.addClassName("stack-item");
        row.getStyle()
                .set("margin", "0")
                .set("padding", "0.5rem 0.75rem")
                .set("border-radius", "4px")
                .set("background", "#f8f9fa")
                .set("color", "#212529")
                .set("font-size", "0.9rem")
                .set("border", "1px solid #e9ecef");
        return row;
    }

    private static void applySurface(Div surface) {
        surface.getStyle()
                .set("padding", "1.25rem")
                .set("background", "white")
                .set("border", "1px solid #dee2e6")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)");
    }
}
