package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
        getStyle().set("background", "transparent");

        add(TavernComponents.createPageLayout("Carte & Menu", menuBoard.title(), menuBoard.chefNote()));
        add(TavernComponents.createSection("L'ardoise du jour", createMenuGrid(menuBoard)));
    }

    private HorizontalLayout createMenuGrid(MenuBoard menuBoard) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);

        for (MenuSection section : menuBoard.sections()) {
            Div card = createMenuCard(section);
            layout.add(card);
            layout.setFlexGrow(1, card);
        }
        return layout;
    }

    private Div createMenuCard(MenuSection section) {
        H3 title = new H3(section.title());
        title.getStyle().set("margin", "0").set("color", "#007bff").set("font-size", "1.2rem");

        Paragraph description = new Paragraph(section.description());
        description.getStyle().set("margin", "0.25rem 0 1rem").set("color", "#6c757d").set("font-size", "0.9rem");

        VerticalLayout entries = new VerticalLayout();
        entries.setPadding(false);
        entries.setSpacing(false);
        entries.getStyle().set("gap", "0.75rem");

        section.entries().stream().map(this::createMenuEntry).forEach(entries::add);

        Div card = new Div(title, description, entries);
        card.setWidthFull();
        card.getStyle()
                .set("padding", "1.25rem")
                .set("background", "white")
                .set("border", "1px solid #dee2e6")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)");
        return card;
    }

    private Div createMenuEntry(MenuEntry entry) {
        Span name = new Span(entry.name());
        name.getStyle().set("font-weight", "600").set("color", "#212529");

        Span price = new Span(entry.price());
        price.getStyle().set("color", "#007bff").set("font-weight", "700");

        Div topLine = new Div(name, price);
        topLine.setWidthFull();
        topLine.getStyle().set("display", "flex").set("justify-content", "space-between").set("align-items", "baseline");

        Paragraph note = new Paragraph(entry.note());
        note.getStyle().set("margin", "0").set("color", "#6c757d").set("font-size", "0.85rem").set("font-style", "italic");

        Div row = new Div(topLine, note);
        row.setWidthFull();
        row.getStyle()
                .set("padding-bottom", "0.5rem")
                .set("border-bottom", "1px solid #f8f9fa");
        return row;
    }
}
