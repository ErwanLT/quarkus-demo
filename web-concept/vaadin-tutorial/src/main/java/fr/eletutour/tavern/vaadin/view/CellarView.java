package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
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
import fr.eletutour.tavern.vaadin.service.StockUpdatedEvent;
import fr.eletutour.tavern.vaadin.service.TavernBroadcaster;
import fr.eletutour.tavern.vaadin.service.TavernService;
import java.util.function.Consumer;

@PageTitle("Cave et futailles")
@Route(value = "cellar", layout = MainLayout.class)
public class CellarView extends VerticalLayout {

    private final TavernBroadcaster broadcaster;
    private final Div stockPanel = new Div();
    private Consumer<StockUpdatedEvent> listener;

    public CellarView(TavernService tavernService, TavernBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        listener = event -> attachEvent.getUI().access(() -> {
            updateStockPanel(event.stocks());
        });
        broadcaster.registerStockListener(listener);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (listener != null) {
            broadcaster.unregisterStockListener(listener);
            listener = null;
        }
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

        stockPanel.setWidthFull();
        stockPanel.getStyle()
                .set("padding", "1.25rem")
                .set("background", "white")
                .set("border", "1px solid #dee2e6")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)");
        
        updateStockPanel(cellarBoard.stocks());

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

    private void updateStockPanel(java.util.List<CellarStock> stocks) {
        stockPanel.removeAll();
        H3 stockTitle = new H3("Niveaux de cave");
        stockTitle.getStyle().set("margin", "0 0 1rem 0").set("color", "#212529").set("font-size", "1.1rem").set("font-weight", "600");
        stockPanel.add(stockTitle);
        stocks.stream().map(this::createStockRow).forEach(stockPanel::add);
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
