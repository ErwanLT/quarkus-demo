package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.ServiceBoard;
import fr.eletutour.tavern.vaadin.service.TavernService;

@PageTitle("Rythme du service")
@Route(value = "service", layout = MainLayout.class)
public class ServiceView extends VerticalLayout {

    public ServiceView(TavernService tavernService) {
        ServiceBoard serviceBoard = tavernService.getServiceBoard();

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "transparent");

        add(TavernComponents.createPageLayout("Rythme du service",
                "Gestion du flux et de la cadence",
                serviceBoard.shiftReading()));
        
        add(TavernComponents.createSection("Indicateurs de cadence",
                TavernComponents.createMetricGrid(serviceBoard.serviceMetrics())));
        
        add(TavernComponents.createSection("Organisation du service", createServiceContent(serviceBoard)));
    }

    private VerticalLayout createServiceContent(ServiceBoard serviceBoard) {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        // Team and Reading side by side (50/50)
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        Div staffing = TavernComponents.createListPanel("Répartition de l'équipe", serviceBoard.teamAssignments());
        staffing.setWidthFull();

        Div reading = TavernComponents.createPanel(serviceBoard.shiftReadingTitle(), serviceBoard.shiftReading());
        reading.setWidthFull();

        row.add(staffing, reading);
        row.setFlexGrow(1, staffing);
        row.setFlexGrow(1, reading);

        mainLayout.add(row);
        return mainLayout;
    }
}
