package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.CellarBoard;
import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.service.TavernService;

@PageTitle("Cave et futailles")
@Route(value = "cellar", layout = MainLayout.class)
public class CellarView extends VerticalLayout {

    public CellarView(TavernService tavernService) {
        CellarBoard cellarBoard = tavernService.getCellarBoard();

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "transparent");

        add(TavernComponents.createPageLayout("Cave et futailles",
                "La cave guide la salle, pas l'inverse",
                cellarBoard.cellarNote()));
        
        add(TavernComponents.createSection("Gestion des stocks et actions", createCellarContent(cellarBoard)));
    }

    private VerticalLayout createCellarContent(CellarBoard cellarBoard) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.setSpacing(true);

        // Top row: Stocks and Notes
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setSpacing(true);

        Div stockPanel = new Div();
        stockPanel.setWidthFull();
        stockPanel.getStyle()
                .set("padding", "1.25rem")
                .set("background", "white")
                .set("border", "1px solid #dee2e6")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)");
        
        H3 stockTitle = new H3("Niveaux de cave");
        stockTitle.getStyle().set("margin", "0 0 1rem 0").set("color", "#212529").set("font-size", "1.1rem").set("font-weight", "600");
        stockPanel.add(stockTitle);
        cellarBoard.stocks().stream().map(this::createStockRow).forEach(stockPanel::add);

        Div notePanel = TavernComponents.createPanel(cellarBoard.cellarNoteTitle(), cellarBoard.cellarNote());
        notePanel.setWidthFull();

        topRow.add(stockPanel, notePanel);
        topRow.setFlexGrow(1, stockPanel);
        topRow.setFlexGrow(1, notePanel);

        // Bottom row: Tasks
        Div tasks = TavernComponents.createListPanel("Actions en cave", cellarBoard.cellarTasks());
        tasks.setWidthFull();

        content.add(topRow, tasks);
        return content;
    }

    private Div createStockRow(CellarStock stock) {
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

        Paragraph note = new Paragraph(stock.note());
        note.setClassName("menu-entry-note");

        Div row = new Div(header, progressTrack, note);
        row.setClassName("stock-row");
        return row;
    }
}
