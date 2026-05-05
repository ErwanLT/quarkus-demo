package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import fr.eletutour.tavern.vaadin.service.TavernService;

public class MainLayout extends AppLayout {

    public MainLayout(TavernService tavernService) {
        addClassName("tavern-app-layout");
        addToNavbar(createHeader());
        addToDrawer(createDrawer(tavernService));
        setPrimarySection(Section.DRAWER);
        getStyle()
                .set("background-color", "#f8f9fa")
                .set("color", "#212529");
    }

    private Div createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("header-toggle");

        Span eyebrow = new Span("Maison et exploitation");
        eyebrow.addClassName("brand-eyebrow");

        H2 title = new H2("The Falling Whale");
        title.addClassName("brand-title");

        Paragraph subtitle = new Paragraph("Gestion et pilotage d'établissement");
        subtitle.addClassName("brand-subtitle");
        subtitle.getStyle().set("margin", "0").set("color", "#6c757d").set("font-size", "0.9rem");

        Div copy = new Div(eyebrow, title, subtitle);
        copy.addClassName("brand-copy");
        eyebrow.getStyle()
                .set("color", "#007bff")
                .set("font-size", "0.75rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em")
                .set("font-weight", "700");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#212529")
                .set("font-size", "1.5rem")
                .set("line-height", "1.1");
        copy.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "0.1rem");

        Div header = new Div(toggle, copy);
        header.addClassName("topbar-shell");
        toggle.getStyle().set("color", "#212529");
        header.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "1rem")
                .set("width", "100%")
                .set("padding", "0.75rem 1.5rem")
                .set("background", "white")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)")
                .set("border-bottom", "1px solid #dee2e6");
        return header;
    }

    private VerticalLayout createDrawer(TavernService tavernService) {
        Span sectionLabel = new Span("Menu Principal");
        sectionLabel.addClassName("drawer-section-label");

        SideNav navigation = new SideNav();
        navigation.addClassName("drawer-nav");
        navigation.addItem(new SideNavItem("Tableau de bord", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        navigation.addItem(new SideNavItem("Carte & Menu", MenuView.class, VaadinIcon.MENU.create()));
        navigation.addItem(new SideNavItem("Réservations", ReservationsView.class, VaadinIcon.CALENDAR.create()));
        navigation.addItem(new SideNavItem("Stocks & Cave", CellarView.class, VaadinIcon.STORAGE.create()));
        navigation.addItem(new SideNavItem("Service", ServiceView.class, VaadinIcon.RECORDS.create()));

        var serviceBoard = tavernService.getServiceBoard();
        Div drawerHighlight = new Div(
                new Span(serviceBoard.drawerAlertTitle()),
                new Paragraph(serviceBoard.drawerAlertDescription()));
        drawerHighlight.addClassName("drawer-highlight");
        drawerHighlight.getStyle()
                .set("padding", "1rem")
                .set("margin", "1rem")
                .set("border-radius", "8px")
                .set("background", "#e7f3ff")
                .set("border", "1px solid #b8daff");
        drawerHighlight.getElement().getChild(0).getStyle()
                .set("display", "block")
                .set("color", "#004085")
                .set("font-weight", "700")
                .set("margin-bottom", "0.2rem");
        drawerHighlight.getElement().getChild(1).getStyle()
                .set("margin", "0")
                .set("color", "#004085")
                .set("font-size", "0.9rem")
                .set("line-height", "1.4");

        VerticalLayout drawer = new VerticalLayout(sectionLabel, navigation, drawerHighlight);
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.addClassName("drawer-shell");
        drawer.getStyle()
                .set("background", "white")
                .set("border-right", "1px solid #dee2e6")
                .set("height", "100%");
        sectionLabel.getStyle()
                .set("padding", "1.5rem 1rem 0.5rem")
                .set("color", "#6c757d")
                .set("font-size", "0.7rem")
                .set("text-transform", "uppercase")
                .set("font-weight", "700");
        navigation.getStyle().set("width", "100%").set("padding", "0 0.5rem");
        return drawer;
    }
}
