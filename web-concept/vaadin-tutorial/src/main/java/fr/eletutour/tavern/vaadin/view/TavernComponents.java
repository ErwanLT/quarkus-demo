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
                .set("gap", "0.3rem")
                .set("padding", "1.55rem 1.7rem")
                .set("border", "1px solid rgba(224, 177, 119, 0.16)")
                .set("border-radius", "24px")
                .set("background",
                        "radial-gradient(circle at top right, rgba(203, 123, 58, 0.16), transparent 30%), linear-gradient(135deg, rgba(62, 39, 28, 0.92) 0%, rgba(26, 19, 16, 0.94) 100%)")
                .set("box-shadow", "0 24px 50px rgba(0, 0, 0, 0.26)");
        kicker.getStyle()
                .set("color", "#d4a36f")
                .set("font-size", "0.82rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.11em")
                .set("font-weight", "700");
        heading.getStyle()
                .set("margin", "0")
                .set("color", "#fff8f0")
                .set("font-size", "clamp(2rem, 4vw, 3.2rem)")
                .set("line-height", "1");
        text.getStyle()
                .set("margin", "0")
                .set("max-width", "60rem")
                .set("color", "#d8c7b3")
                .set("line-height", "1.6")
                .set("font-size", "1.02rem");
        return header;
    }

    static Div createSection(String title, Component... content) {
        H2 heading = new H2(title);
        heading.addClassName("section-title");

        Div section = new Div();
        section.addClassName("section-block");
        section.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "1rem")
                .set("margin-top", "0.5rem");
        heading.getStyle().set("margin", "0").set("color", "#f3e7d7").set("font-size", "1.45rem");
        section.add(heading);
        for (Component component : content) {
            section.add(component);
        }
        return section;
    }

    static Div createMetricGrid(List<HighlightMetric> metrics) {
        Div grid = new Div();
        grid.addClassName("metrics-grid");
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(220px, 1fr))")
                .set("gap", "1rem");
        metrics.stream()
                .map(TavernComponents::createMetricCard)
                .forEach(grid::add);
        return grid;
    }

    static Div createMetricCard(HighlightMetric metric) {
        Span label = new Span(metric.label());
        label.addClassName("metric-label");

        H3 value = new H3(metric.value());
        value.addClassName("metric-value");

        Paragraph detail = new Paragraph(metric.detail());
        detail.addClassName("metric-detail");

        Div card = new Div(label, value, detail);
        card.addClassName("metric-card");
        card.getStyle()
                .set("padding", "1.15rem")
                .set("border", "1px solid rgba(220, 175, 118, 0.14)")
                .set("border-radius", "20px")
                .set("background", "linear-gradient(180deg, rgba(31, 22, 18, 0.92) 0%, rgba(22, 17, 14, 0.94) 100%)")
                .set("box-shadow", "0 18px 35px rgba(0, 0, 0, 0.2)");
        label.getStyle()
                .set("display", "block")
                .set("margin-bottom", "0.5rem")
                .set("color", "#bc8b5b")
                .set("font-size", "0.78rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em")
                .set("font-weight", "700");
        value.getStyle().set("margin", "0").set("color", "#fff8f0").set("font-size", "2rem");
        detail.getStyle().set("margin", "0.4rem 0 0").set("color", "#cfb8a0").set("line-height", "1.45");
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
        heading.getStyle().set("margin", "0").set("color", "#fff4e8").set("font-size", "1.45rem");
        text.getStyle().set("margin", "0.45rem 0 0").set("color", "#d1bdab").set("line-height", "1.6");
        return panel;
    }

    static Div createListPanel(String title, List<String> items) {
        H3 heading = new H3(title);
        heading.addClassName("panel-title");

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);
        list.addClassName("stack-list");
        list.getStyle().set("margin-top", "0.95rem").set("gap", "0.7rem");
        items.stream().map(TavernComponents::createListItem).forEach(list::add);

        Div panel = new Div(heading, list);
        panel.addClassName("panel-card");
        applySurface(panel);
        heading.getStyle().set("margin", "0").set("color", "#fff4e8").set("font-size", "1.45rem");
        return panel;
    }

    private static Paragraph createListItem(String item) {
        Paragraph row = new Paragraph(item);
        row.addClassName("stack-item");
        row.getStyle()
                .set("margin", "0")
                .set("padding", "0.9rem 1rem")
                .set("border-radius", "14px")
                .set("background", "rgba(255, 255, 255, 0.04)")
                .set("color", "#f1e6d7")
                .set("border", "1px solid rgba(221, 177, 118, 0.08)");
        return row;
    }

    private static void applySurface(Div surface) {
        surface.getStyle()
                .set("padding", "1.25rem")
                .set("border", "1px solid rgba(220, 175, 118, 0.14)")
                .set("border-radius", "20px")
                .set("background", "linear-gradient(180deg, rgba(31, 22, 18, 0.92) 0%, rgba(22, 17, 14, 0.94) 100%)")
                .set("box-shadow", "0 18px 35px rgba(0, 0, 0, 0.2)");
    }
}
