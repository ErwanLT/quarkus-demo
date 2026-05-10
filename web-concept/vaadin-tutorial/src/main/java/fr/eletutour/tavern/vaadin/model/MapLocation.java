package fr.eletutour.tavern.vaadin.model;

public record MapLocation(
        String name,
        double latitude,
        double longitude,
        String description,
        String type // e.g., "Tavern", "POI", "Danger"
) {
}
