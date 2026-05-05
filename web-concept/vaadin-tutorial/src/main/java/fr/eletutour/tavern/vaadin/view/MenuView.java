package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.MenuBoard;
import fr.eletutour.tavern.vaadin.model.MenuEntry;
import fr.eletutour.tavern.vaadin.model.MenuSection;
import fr.eletutour.tavern.vaadin.service.TavernService;

@PageTitle("Carte et ardoise")
@Route(value = "menu", layout = MainLayout.class)
public class MenuView extends VerticalLayout {

    public MenuView(TavernService tavernService) {
        MenuBoard menuBoard = tavernService.getMenuBoard();

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(TavernComponents.createPageLayout("Carte et ardoise", menuBoard.title(), menuBoard.chefNote()));
        add(createMenuGrid(menuBoard));
    }

    private Div createMenuGrid(MenuBoard menuBoard) {
        Div grid = new Div();
        grid.addClassName("menu-grid");

        for (MenuSection section : menuBoard.sections()) {
            grid.add(createMenuCard(section));
        }
        return grid;
    }

    private Div createMenuCard(MenuSection section) {
        H3 title = new H3(section.title());
        title.addClassName("panel-title");

        Paragraph description = new Paragraph(section.description());
        description.addClassName("panel-copy");

        VerticalLayout entries = new VerticalLayout();
        entries.setPadding(false);
        entries.setSpacing(false);
        entries.addClassName("menu-entries");

        section.entries().stream().map(this::createMenuEntry).forEach(entries::add);

        Div card = new Div(title, description, entries);
        card.addClassName("menu-card");
        return card;
    }

    private Div createMenuEntry(MenuEntry entry) {
        Span name = new Span(entry.name());
        name.addClassName("menu-entry-name");

        Span price = new Span(entry.price());
        price.addClassName("menu-entry-price");

        Div topLine = new Div(name, price);
        topLine.addClassName("menu-entry-header");

        Paragraph note = new Paragraph(entry.note());
        note.addClassName("menu-entry-note");

        Div row = new Div(topLine, note);
        row.addClassName("menu-entry");
        return row;
    }
}
