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

        add(TavernComponents.createPageLayout("Salle commune", snapshot.heroTitle(), snapshot.heroDescription()));
        add(createActions());
        add(TavernComponents.createSection("Les chiffres qui tiennent le comptoir",
                TavernComponents.createMetricGrid(snapshot.metrics())));
        add(createDashboardGrid(snapshot));
    }

    private HorizontalLayout createActions() {
        Button serviceCall = new Button("Lancer l'appel du service", VaadinIcon.MEGAPHONE.create(),
                event -> Notification.show("Le personnel de salle est prevenu pour le coup de feu."));
        serviceCall.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cellarCheck = new Button("Passer la cave en revue", VaadinIcon.BAR_CHART.create(),
                event -> Notification.show("La cave remonte les references sous tension."));
        cellarCheck.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(serviceCall, cellarCheck);
        actions.addClassName("page-actions");
        actions.setPadding(false);
        actions.setSpacing(true);
        return actions;
    }

    private Div createDashboardGrid(DashboardSnapshot snapshot) {
        Div grid = new Div();
        grid.addClassName("dashboard-grid");

        Div story = TavernComponents.createPanel(snapshot.roomHeadline(), snapshot.roomStory());
        story.addClassName("span-8");

        Div priorities = TavernComponents.createListPanel("Priorites immediates", snapshot.priorities());
        priorities.addClassName("span-4");

        Div highlights = TavernComponents.createListPanel("Signaux du soir", snapshot.tonightHighlights());
        highlights.addClassName("span-12");

        grid.add(story, priorities, highlights);
        return grid;
    }
}
