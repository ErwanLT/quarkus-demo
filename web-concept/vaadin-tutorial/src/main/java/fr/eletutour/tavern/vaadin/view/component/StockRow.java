package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import fr.eletutour.tavern.vaadin.model.CellarStock;

/**
 * A custom component to display a stock level with a progress bar.
 */
public class StockRow extends Div {

    public StockRow(CellarStock stock) {
        this(stock, false);
    }

    public StockRow(CellarStock stock, boolean showNote) {
        setClassName("stock-row");

        Span product = new Span(stock.productName());
        product.setClassName("stock-name");

        Span level = new Span(stock.currentLevel() + " / " + stock.maxLevel() + " " + stock.unit());
        level.setClassName("stock-level");

        Div header = new Div(product, level);
        header.setClassName("stock-header");

        Div progressFill = new Div();
        progressFill.setClassName("stock-progress-fill");
        int percentage = (int) Math.round((stock.currentLevel() * 100.0) / stock.maxLevel());
        String color = percentage < 25 ? "var(--stock-low)" : (percentage < 50 ? "var(--stock-medium)" : "var(--stock-high)");
        progressFill.getStyle().set("width", percentage + "%").set("background-color", color);

        Div progressTrack = new Div(progressFill);
        progressTrack.setClassName("stock-progress-track");

        add(header, progressTrack);

        if (showNote && stock.note() != null && !stock.note().isEmpty()) {
            Paragraph note = new Paragraph(stock.note());
            note.setClassName("menu-entry-note");
            add(note);
        }
    }
}
