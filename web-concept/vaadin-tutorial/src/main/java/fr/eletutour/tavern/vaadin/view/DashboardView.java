package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
        
        HorizontalLayout actions = createActions();
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

    private HorizontalLayout createActions() {
        Button serviceCall = new Button("Lancer l'appel du service", VaadinIcon.MEGAPHONE.create(),
                event -> Notification.show("Le personnel de salle est prévenu."));
        serviceCall.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cellarCheck = new Button("État de la cave", VaadinIcon.BAR_CHART.create(),
                event -> Notification.show("Chargement des références sous tension..."));
        cellarCheck.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(serviceCall, cellarCheck);
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.setWidthFull();
        return actions;
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
