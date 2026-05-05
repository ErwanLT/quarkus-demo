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
        hero.setWidthFull();
        add(hero);
        
        HorizontalLayout actions = createActions(tavernService);
        actions.setWidthFull();
        actions.getStyle().set("padding", "0 1.5rem").set("margin-bottom", "1rem");
        add(actions);

        // Metrics Section - Full width guaranteed
        var metricsSection = TavernComponents.createSection("Indicateurs de performance", TavernComponents.createMetricGrid(snapshot.metrics()));
        metricsSection.setWidthFull();
        add(metricsSection);
        
        var mainGridSection = TavernComponents.createSection("Analyse de l'activité", createDashboardGrid(snapshot));
        mainGridSection.setWidthFull();
        add(mainGridSection);
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
                .set("border-radius", "4px");

        Div row = new Div(header, progressTrack);
        row.setWidthFull();
        row.getStyle()
                .set("padding", "0.75rem 0")
                .set("border-bottom", "1px solid #f8f9fa");
        return row;
    }

    private VerticalLayout createDashboardGrid(DashboardSnapshot snapshot) {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        // Story and Priorities on one row
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

        // Highlights on its own row below
        Div highlights = TavernComponents.createListPanel("Signaux du soir", snapshot.tonightHighlights());
        highlights.setWidthFull();

        mainLayout.add(topRow, highlights);
        return mainLayout;
    }
}
