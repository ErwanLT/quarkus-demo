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
        product.getStyle().set("font-weight", "600").set("color", "#212529");

        Span level = new Span(stock.currentLevel() + " / " + stock.maxLevel() + " " + stock.unit());
        level.getStyle().set("color", "#6c757d").set("font-size", "0.85rem");

        Div header = new Div(product, level);
        header.setWidthFull();
        header.getStyle().set("display", "flex").set("justify-content", "space-between").set("align-items", "baseline").set("margin-bottom", "0.25rem");

        Div progressFill = new Div();
        int percentage = (int) Math.round((stock.currentLevel() * 100.0) / stock.maxLevel());
        String color = percentage < 25 ? "#dc3545" : (percentage < 50 ? "#ffc107" : "#28a745");
        
        progressFill.getStyle()
                .set("width", percentage + "%")
                .set("height", "100%")
                .set("background-color", color)
                .set("border-radius", "4px");

        Div progressTrack = new Div(progressFill);
        progressTrack.setWidthFull();
        progressTrack.getStyle()
                .set("height", "8px")
                .set("background-color", "#e9ecef")
                .set("border-radius", "4px")
                .set("margin-bottom", "0.5rem");

        Paragraph note = new Paragraph(stock.note());
        note.getStyle().set("margin", "0").set("color", "#6c757d").set("font-size", "0.8rem").set("font-style", "italic");

        Div row = new Div(header, progressTrack, note);
        row.setWidthFull();
        row.getStyle()
                .set("padding", "0.75rem 0")
                .set("border-bottom", "1px solid #f8f9fa");
        return row;
    }
}
