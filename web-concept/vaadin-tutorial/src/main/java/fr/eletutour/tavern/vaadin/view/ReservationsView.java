package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.ReservationBoard;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import fr.eletutour.tavern.vaadin.service.TavernService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@PageTitle("Reservations")
@Route(value = "reservations", layout = MainLayout.class)
public class ReservationsView extends VerticalLayout {

    private final Grid<ReservationEntry> grid = new Grid<>(ReservationEntry.class, false);

    public ReservationsView(TavernService tavernService) {
        ReservationBoard reservationBoard = tavernService.getReservationBoard();

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "transparent");

        add(TavernComponents.createPageLayout("Reservations",
                "Le livre de salle",
                reservationBoard.summary()));
        
        Button addBtn = new Button("Nouvelle réservation", VaadinIcon.PLUS.create(), e -> openReservationForm(tavernService));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        HorizontalLayout actions = new HorizontalLayout(addBtn);
        actions.getStyle().set("padding", "0 1.5rem").set("margin-bottom", "0.5rem");
        add(actions);

        Div gridContainer = createReservationGrid(reservationBoard);
        add(TavernComponents.createSection("Tableau de reservations", gridContainer));
        
        add(TavernComponents.createSection("Notes de maitre d'hotel",
                TavernComponents.createListPanel("Consignes de placement", reservationBoard.hostNotes())));
    }

    private void openReservationForm(TavernService tavernService) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Ajouter une réservation");

        TextField name = new TextField("Nom de la compagnie");
        IntegerField count = new IntegerField("Nombre de convives");
        count.setMin(1);
        count.setValue(2);

        TimePicker arrival = new TimePicker("Heure d'arrivée");
        arrival.setValue(LocalTime.of(19, 30));

        Select<String> area = new Select<>();
        area.setLabel("Zone de placement");
        area.setItems("Coin du feu", "Grande table", "Balcon intérieur", "Arrière-salle", "Patio");
        area.setValue("Grande table");

        Select<String> status = new Select<>();
        status.setLabel("Statut");
        status.setItems("Confirmée", "VIP", "Rapide", "En attente");
        status.setValue("Confirmée");

        TextArea note = new TextArea("Notes particulières");
        note.setPlaceholder("Ex: Allergies, anniversaire...");

        VerticalLayout form = new VerticalLayout(name, new HorizontalLayout(count, arrival), new HorizontalLayout(area, status), note);
        form.setPadding(false);
        form.setSpacing(true);
        form.setAlignItems(Alignment.STRETCH);
        dialog.add(form);

        Button save = new Button("Enregistrer", e -> {
            if (name.getValue().isEmpty()) {
                Notification.show("Veuillez saisir un nom.");
                return;
            }
            ReservationEntry entry = new ReservationEntry(
                    name.getValue(),
                    count.getValue(),
                    arrival.getValue().format(DateTimeFormatter.ofPattern("HH:mm")),
                    area.getValue(),
                    status.getValue(),
                    note.getValue()
            );
            tavernService.addReservation(entry);
            grid.setItems(tavernService.getReservationBoard().reservations());
            Notification.show("Réservation enregistrée !");
            dialog.close();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancel = new Button("Annuler", e -> dialog.close());
        dialog.getFooter().add(cancel, save);

        dialog.open();
    }

    private Div createReservationGrid(ReservationBoard reservationBoard) {
        grid.addColumn(ReservationEntry::guestName).setHeader("Compagnie").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(ReservationEntry::guestCount).setHeader("Convives").setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER).setAutoWidth(true);
        grid.addColumn(ReservationEntry::arrivalTime).setHeader("Arrivée").setAutoWidth(true);
        grid.addColumn(ReservationEntry::area).setHeader("Zone").setAutoWidth(true);
        grid.addColumn(ReservationEntry::status).setHeader("Statut").setAutoWidth(true);
        grid.addColumn(ReservationEntry::note).setHeader("Note").setFlexGrow(2);
        
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setItems(reservationBoard.reservations());
        grid.setWidthFull();
        grid.setAllRowsVisible(true);
        grid.getStyle().set("border-radius", "8px").set("border", "1px solid #dee2e6");

        Div wrapper = new Div(grid);
        wrapper.setWidthFull();
        wrapper.getStyle().set("padding", "0");
        return wrapper;
    }
}
