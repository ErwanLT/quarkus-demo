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
                .set("background",
                        "radial-gradient(circle at top right, rgba(181,109,42,0.18), transparent 28%), linear-gradient(180deg, #120d0a 0%, #17110d 100%)")
                .set("color", "#efe3d3");
    }

    private Div createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("header-toggle");

        Span eyebrow = new Span("Maison et exploitation");
        eyebrow.addClassName("brand-eyebrow");

        H2 title = new H2("La Taverne du Griffon");
        title.addClassName("brand-title");

        Paragraph subtitle = new Paragraph("Une interface de taverne, pas une demo neutre.");
        subtitle.addClassName("brand-subtitle");
        subtitle.getStyle().set("margin", "0").set("color", "#d8c0a6").set("font-size", "0.98rem");

        Div copy = new Div(eyebrow, title, subtitle);
        copy.addClassName("brand-copy");
        eyebrow.getStyle()
                .set("color", "#c99d6a")
                .set("font-size", "0.78rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.08em")
                .set("font-weight", "700");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#fff7ef")
                .set("font-size", "1.75rem")
                .set("line-height", "1.1");
        copy.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "0.18rem");

        Div header = new Div(toggle, copy);
        header.addClassName("topbar-shell");
        toggle.getStyle().set("color", "#f4dfc9");
        header.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "1rem")
                .set("width", "min(100%, 1400px)")
                .set("margin", "1rem auto 0")
                .set("padding", "1rem 1.25rem")
                .set("border", "1px solid rgba(214, 170, 118, 0.18)")
                .set("border-radius", "20px")
                .set("background", "linear-gradient(135deg, rgba(65, 41, 29, 0.92) 0%, rgba(34, 24, 19, 0.92) 100%)")
                .set("box-shadow", "0 20px 45px rgba(0, 0, 0, 0.28)");
        return header;
    }

    private VerticalLayout createDrawer(TavernService tavernService) {
        Span sectionLabel = new Span("Navigation");
        sectionLabel.addClassName("drawer-section-label");

        SideNav navigation = new SideNav();
        navigation.addClassName("drawer-nav");
        navigation.addItem(new SideNavItem("Salle commune", DashboardView.class, VaadinIcon.HOME.create()));
        navigation.addItem(new SideNavItem("Carte et ardoise", MenuView.class, VaadinIcon.CUTLERY.create()));
        navigation.addItem(new SideNavItem("Reservations", ReservationsView.class, VaadinIcon.CALENDAR.create()));
        navigation.addItem(new SideNavItem("Cave et futailles", CellarView.class, VaadinIcon.ARCHIVES.create()));
        navigation.addItem(new SideNavItem("Rythme du service", ServiceView.class, VaadinIcon.CLIPBOARD_TEXT.create()));

        var serviceBoard = tavernService.getServiceBoard();
        Div drawerHighlight = new Div(
                new Span(serviceBoard.drawerAlertTitle()),
                new Paragraph(serviceBoard.drawerAlertDescription()));
        drawerHighlight.addClassName("drawer-highlight");
        drawerHighlight.getStyle()
                .set("padding", "1rem")
                .set("border-radius", "16px")
                .set("border", "1px solid rgba(231, 176, 117, 0.18)")
                .set("background", "linear-gradient(180deg, rgba(96, 53, 31, 0.95) 0%, rgba(52, 31, 23, 0.95) 100%)")
                .set("box-shadow", "inset 0 1px 0 rgba(255, 240, 220, 0.06)");
        drawerHighlight.getElement().getChild(0).getStyle()
                .set("display", "block")
                .set("color", "#fff5ea")
                .set("font-weight", "700")
                .set("margin-bottom", "0.3rem");
        drawerHighlight.getElement().getChild(1).getStyle()
                .set("margin", "0")
                .set("color", "#dfc5a6")
                .set("line-height", "1.5");

        VerticalLayout drawer = new VerticalLayout(sectionLabel, navigation, drawerHighlight);
        drawer.setPadding(false);
        drawer.setSpacing(true);
        drawer.addClassName("drawer-shell");
        drawer.getStyle()
                .set("padding", "1.4rem 1rem 1rem")
                .set("gap", "1rem")
                .set("background", "linear-gradient(180deg, rgba(30, 21, 17, 0.98) 0%, rgba(20, 15, 12, 0.98) 100%)")
                .set("min-height", "100%")
                .set("border-right", "1px solid rgba(207, 162, 113, 0.18)");
        sectionLabel.getStyle()
                .set("color", "#b78956")
                .set("font-size", "0.78rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.12em")
                .set("font-weight", "700");
        navigation.getStyle().set("width", "100%");
        return drawer;
    }
}
