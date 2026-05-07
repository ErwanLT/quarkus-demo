package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.CellarBoard;
import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.model.DashboardSnapshot;
import fr.eletutour.tavern.vaadin.service.TavernService;

@PageTitle("Salle commune")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView(TavernService tavernService) {
        DashboardSnapshot snapshot = tavernService.getDashboard();

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "transparent");

        var hero = TavernComponents.createPageLayout("Tableau de bord", snapshot.heroTitle(), snapshot.heroDescription());
        add(hero);
        
        HorizontalLayout actions = createActions(tavernService);
        add(actions);

        // Metrics Section
        add(TavernComponents.createSection("Indicateurs de performance", TavernComponents.createMetricGrid(snapshot.metrics())));
        
        // Main Activity Grid
        add(TavernComponents.createSection("Analyse de l'activité", createDashboardGrid(snapshot)));
    }

    private HorizontalLayout createActions(TavernService tavernService) {
        Button serviceCall = new Button("Lancer l'appel du service", VaadinIcon.MEGAPHONE.create(),
                event -> Notification.show("Le personnel de salle est prévenu."));
        serviceCall.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Dialog cellarDialog = createCellarDialog(tavernService);
        Button cellarCheck = new Button("État de la cave", VaadinIcon.BAR_CHART.create(),
                event -> cellarDialog.open());
        cellarCheck.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(serviceCall, cellarCheck);
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.setClassName("dashboard-actions");
        actions.setWidthFull();
        return actions;
    }

    private Dialog createCellarDialog(TavernService tavernService) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("État des stocks - Cave & Futailles");
        dialog.setWidth("600px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setWidthFull();

        CellarBoard cellarBoard = tavernService.getCellarBoard();
        cellarBoard.stocks().forEach(stock -> dialogLayout.add(createStockRow(stock)));

        dialog.add(dialogLayout);

        Button closeButton = new Button("Fermer", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        return dialog;
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

        Div row = new Div(header, progressTrack);
        row.setClassName("stock-row");
        return row;
    }

    private VerticalLayout createDashboardGrid(DashboardSnapshot snapshot) {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setSpacing(true);

        Div story = TavernComponents.createPanel(snapshot.roomHeadline(), snapshot.roomStory());
        story.setWidthFull();
        Div priorities = TavernComponents.createListPanel("Priorités immédiates", snapshot.priorities());
        priorities.setWidthFull();

        topRow.add(story, priorities);
        topRow.setFlexGrow(1, story);
        topRow.setFlexGrow(1, priorities);

        Div highlights = TavernComponents.createListPanel("Signaux du soir", snapshot.tonightHighlights());
        highlights.setWidthFull();

        mainLayout.add(topRow, highlights);
        return mainLayout;
    }
}
