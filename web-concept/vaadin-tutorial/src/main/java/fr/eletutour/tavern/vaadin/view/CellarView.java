package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
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

        add(TavernComponents.createPageLayout("Cave et futailles",
                "La cave guide la salle, pas l'inverse",
                cellarBoard.cellarNote()));
        add(createCellarGrid(cellarBoard));
    }

    private Div createCellarGrid(CellarBoard cellarBoard) {
        Div grid = new Div();
        grid.addClassName("dashboard-grid");

        Div stockPanel = new Div();
        stockPanel.addClassNames("panel-card", "span-8");
        H3 title = new H3("Niveaux de cave");
        title.addClassName("panel-title");
        stockPanel.add(title);
        cellarBoard.stocks().stream().map(this::createStockRow).forEach(stockPanel::add);

        Div notePanel = TavernComponents.createPanel(cellarBoard.cellarNoteTitle(), cellarBoard.cellarNote());
        notePanel.addClassName("span-4");

        Div tasks = TavernComponents.createListPanel("Actions en cave", cellarBoard.cellarTasks());
        tasks.addClassName("span-12");

        grid.add(stockPanel, notePanel, tasks);
        return grid;
    }

    private Div createStockRow(CellarStock stock) {
        Span product = new Span(stock.productName());
        product.addClassName("stock-name");

        Span level = new Span(stock.currentLevel() + " / " + stock.maxLevel() + " " + stock.unit());
        level.addClassName("stock-level");

        Div header = new Div(product, level);
        header.addClassName("stock-header");

        Div progressFill = new Div();
        progressFill.addClassName("stock-progress-fill");
        int percentage = (int) Math.round((stock.currentLevel() * 100.0) / stock.maxLevel());
        progressFill.getStyle().set("width", percentage + "%");

        Div progressTrack = new Div(progressFill);
        progressTrack.addClassName("stock-progress-track");

        Paragraph note = new Paragraph(stock.note());
        note.addClassName("stock-note");

        Div row = new Div(header, progressTrack, note);
        row.addClassName("stock-row");
        return row;
    }
}
