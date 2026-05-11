package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import fr.eletutour.tavern.vaadin.service.SecurityService;
import fr.eletutour.tavern.vaadin.service.TavernService;
import jakarta.inject.Inject;

public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final SecurityService securityService;

    @Inject
    public MainLayout(TavernService tavernService, SecurityService securityService) {
        this.securityService = securityService;
        addClassName("tavern-app-layout");
        addToNavbar(createHeader());
        addToDrawer(createDrawer(tavernService));
        setPrimarySection(Section.DRAWER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (securityService.getAuthenticatedUser().isAnonymous()) {
            event.rerouteTo(LoginView.class);
        }
    }

    private Div createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("header-toggle");

        Span eyebrow = new Span("Maison et exploitation");
        eyebrow.setClassName("brand-eyebrow");

        H2 title = new H2("The Falling Whale");
        title.setClassName("brand-title");

        Paragraph subtitle = new Paragraph("Gestion et pilotage d'établissement");
        subtitle.setClassName("brand-subtitle");

        Div copy = new Div(eyebrow, title, subtitle);
        copy.setClassName("brand-copy");
        copy.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "0.1rem");

        Button logout = new Button("Déconnexion", e -> securityService.logout());

        Div header = new Div(toggle, copy, logout);
        header.setClassName("topbar-shell");
        header.getStyle().set("display", "flex").set("align-items", "center").set("gap", "1rem");
        header.setWidthFull();
        return header;
    }

    private VerticalLayout createDrawer(TavernService tavernService) {
        Span sectionLabel = new Span("Menu Principal");
        sectionLabel.setClassName("drawer-section-label");

        SideNav navigation = new SideNav();
        navigation.setClassName("drawer-nav");
        navigation.addItem(new SideNavItem("Tableau de bord", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        navigation.addItem(new SideNavItem("Carte & Menu", MenuView.class, VaadinIcon.MENU.create()));
        navigation.addItem(new SideNavItem("Réservations", ReservationsView.class, VaadinIcon.CALENDAR.create()));
        navigation.addItem(new SideNavItem("Exploration", TavernMapView.class, VaadinIcon.ABSOLUTE_POSITION.create()));
        navigation.addItem(new SideNavItem("Analytiques", AnalyticsView.class, VaadinIcon.CHART_LINE.create()));
        navigation.addItem(new SideNavItem("Stocks & Cave", CellarView.class, VaadinIcon.STORAGE.create()));
        navigation.addItem(new SideNavItem("Service", ServiceView.class, VaadinIcon.RECORDS.create()));

        var serviceBoard = tavernService.getServiceBoard();
        Div drawerHighlight = new Div(
                new Span(serviceBoard.drawerAlertTitle()),
                new Paragraph(serviceBoard.drawerAlertDescription()));
        drawerHighlight.setClassName("drawer-highlight");
        drawerHighlight.getElement().getChild(0).getStyle()
                .set("display", "block")
                .set("font-weight", "700")
                .set("margin-bottom", "0.2rem");
        drawerHighlight.getElement().getChild(1).getStyle()
                .set("margin", "0")
                .set("font-size", "0.9rem")
                .set("line-height", "1.4");

        VerticalLayout drawer = new VerticalLayout(sectionLabel, navigation, drawerHighlight);
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.setClassName("drawer-shell");
        drawer.getStyle().set("height", "100%");
        navigation.getStyle().set("width", "100%").set("padding", "0 0.5rem");
        return drawer;
    }
}
