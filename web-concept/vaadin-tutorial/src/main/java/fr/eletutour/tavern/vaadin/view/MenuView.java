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
import fr.eletutour.tavern.vaadin.view.component.MenuEntryRow;

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
        title.setClassName("panel-title");

        Paragraph description = new Paragraph(section.description());
        description.setClassName("panel-description");

        VerticalLayout entries = new VerticalLayout();
        entries.setPadding(false);
        entries.setSpacing(false);
        entries.setClassName("menu-entries-container");

        section.entries().stream().map(MenuEntryRow::new).forEach(entries::add);

        Div card = new Div(title, description, entries);
        card.setClassName("whale-panel");
        return card;
    }
}
