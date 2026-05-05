package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.ReservationEntry;

/**
 * Event fired when a new reservation is added to the system.
 */
public record ReservationAddedEvent(ReservationEntry entry) {
}
