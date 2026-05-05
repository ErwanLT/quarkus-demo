package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.Div;
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

        add(TavernComponents.createPageLayout("Rythme du service",
                "Tenir la cadence sans casser l'ambiance",
                serviceBoard.shiftReading()));
        add(TavernComponents.createSection("Cadence de la maison",
                TavernComponents.createMetricGrid(serviceBoard.serviceMetrics())));
        add(createServiceGrid(serviceBoard));
    }

    private Div createServiceGrid(ServiceBoard serviceBoard) {
        Div grid = new Div();
        grid.addClassName("dashboard-grid");

        Div staffing = TavernComponents.createListPanel("Repartition de l'equipe", serviceBoard.teamAssignments());
        staffing.addClassName("span-6");

        Div reading = TavernComponents.createPanel(serviceBoard.shiftReadingTitle(), serviceBoard.shiftReading());
        reading.addClassName("span-6");

        grid.add(staffing, reading);
        return grid;
    }
}
