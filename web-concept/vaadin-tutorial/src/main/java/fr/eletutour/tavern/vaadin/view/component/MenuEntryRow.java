package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import fr.eletutour.tavern.vaadin.model.MenuEntry;

/**
 * A custom component to display a menu entry.
 */
public class MenuEntryRow extends Div {

    public MenuEntryRow(MenuEntry entry) {
        setWidthFull();
        setClassName("menu-entry-row");

        Span name = new Span(entry.name());
        name.setClassName("menu-entry-name");

        Span price = new Span(entry.price());
        price.setClassName("price");

        Div topLine = new Div(name, price);
        topLine.setClassName("menu-entry-header");

        Paragraph note = new Paragraph(entry.note());
        note.setClassName("menu-entry-note");

        add(topLine, note);
    }
}
