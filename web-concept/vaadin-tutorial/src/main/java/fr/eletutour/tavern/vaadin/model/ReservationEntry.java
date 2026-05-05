package fr.eletutour.tavern.vaadin.model;

public record ReservationEntry(
        String guestName,
        int guestCount,
        String arrivalTime,
        String area,
        String status,
        String note) {
}
