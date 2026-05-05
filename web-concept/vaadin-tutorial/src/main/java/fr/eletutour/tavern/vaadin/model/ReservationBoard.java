package fr.eletutour.tavern.vaadin.model;

import java.util.List;

public record ReservationBoard(
        String summary,
        List<ReservationEntry> reservations,
        List<String> hostNotes) {
}
