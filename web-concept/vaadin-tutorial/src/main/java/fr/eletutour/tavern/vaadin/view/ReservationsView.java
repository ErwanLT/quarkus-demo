package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.ReservationBoard;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import fr.eletutour.tavern.vaadin.service.TavernService;

@PageTitle("Reservations")
@Route(value = "reservations", layout = MainLayout.class)
public class ReservationsView extends VerticalLayout {

    public ReservationsView(TavernService tavernService) {
        ReservationBoard reservationBoard = tavernService.getReservationBoard();

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(TavernComponents.createPageLayout("Reservations",
                "Le livre de salle reste net avant le coup de feu",
                reservationBoard.summary()));
        add(TavernComponents.createSection("Tableau de reservations", createReservationGrid(reservationBoard)));
        add(TavernComponents.createSection("Notes de maitre d'hotel",
                TavernComponents.createListPanel("Consignes de placement", reservationBoard.hostNotes())));
    }

    private Div createReservationGrid(ReservationBoard reservationBoard) {
        Grid<ReservationEntry> grid = new Grid<>(ReservationEntry.class, false);
        grid.addColumn(ReservationEntry::guestName).setHeader("Compagnie").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(ReservationEntry::guestCount).setHeader("Convives").setAutoWidth(true);
        grid.addColumn(ReservationEntry::arrivalTime).setHeader("Arrivee").setAutoWidth(true);
        grid.addColumn(ReservationEntry::area).setHeader("Zone").setAutoWidth(true);
        grid.addColumn(ReservationEntry::status).setHeader("Statut").setAutoWidth(true);
        grid.addColumn(ReservationEntry::note).setHeader("Note").setFlexGrow(1);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.addClassName("tavern-grid");
        grid.setItems(reservationBoard.reservations());
        grid.setWidthFull();

        Div wrapper = new Div(grid);
        wrapper.addClassName("grid-frame");
        return wrapper;
    }
}
