package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
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
import fr.eletutour.tavern.vaadin.service.StockUpdatedEvent;
import fr.eletutour.tavern.vaadin.service.TavernBroadcaster;
import fr.eletutour.tavern.vaadin.service.TavernService;
import fr.eletutour.tavern.vaadin.view.component.StockRow;
import jakarta.annotation.security.RolesAllowed;
import java.util.function.Consumer;

@RolesAllowed("STAFF")
@PageTitle("Salle commune")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    private final TavernService tavernService;
    private final TavernBroadcaster broadcaster;
    private final Div metricsContainer = new Div();
    private final VerticalLayout dialogLayout = new VerticalLayout();
    private Consumer<StockUpdatedEvent> listener;

    public DashboardView(TavernService tavernService, TavernBroadcaster broadcaster) {
        this.tavernService = tavernService;
        this.broadcaster = broadcaster;
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
        updateMetrics(snapshot);
        add(TavernComponents.createSection("Indicateurs de performance", metricsContainer));
        
        // Main Activity Grid
        add(TavernComponents.createSection("Analyse de l'activité", createDashboardGrid(snapshot)));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        listener = event -> attachEvent.getUI().access(() -> {
            updateMetrics(tavernService.getDashboard());
            updateDialogContent(event.stocks());
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

    private void updateMetrics(DashboardSnapshot snapshot) {
        metricsContainer.removeAll();
        metricsContainer.add(TavernComponents.createMetricGrid(snapshot.metrics()));
    }

    private void updateDialogContent(java.util.List<CellarStock> stocks) {
        dialogLayout.removeAll();
        stocks.forEach(stock -> dialogLayout.add(new StockRow(stock)));
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

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setWidthFull();

        CellarBoard cellarBoard = tavernService.getCellarBoard();
        updateDialogContent(cellarBoard.stocks());

        dialog.add(dialogLayout);

        Button closeButton = new Button("Fermer", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        return dialog;
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
