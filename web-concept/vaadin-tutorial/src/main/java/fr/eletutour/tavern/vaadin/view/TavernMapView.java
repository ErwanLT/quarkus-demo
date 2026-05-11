package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.service.TavernService;
import fr.eletutour.tavern.vaadin.view.component.CustomMap;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("STAFF")
@PageTitle("Exploration des environs")
@Route(value = "map", layout = MainLayout.class)
public class TavernMapView extends VerticalLayout {

    public TavernMapView(TavernService tavernService) {
        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "transparent");

        add(TavernComponents.createPageLayout("Exploration", 
                "Les environs de Port-Lune", 
                "Gardez un oeil sur les points d'intérêt et les zones de danger autour de The Falling Whale."));

        CustomMap map = new CustomMap();
        map.setWidthFull();
        map.setHeight("500px");
        map.setLocations(tavernService.getMapLocations());

        add(TavernComponents.createSection("Carte interactive", map));
    }
}
