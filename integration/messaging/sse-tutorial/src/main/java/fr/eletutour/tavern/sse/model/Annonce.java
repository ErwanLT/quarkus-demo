package fr.eletutour.tavern.sse.model;

import java.time.Instant;

public record Annonce(String auteur, String message, Instant horodatage) {

    public static Annonce duBarman(String message) {
        return new Annonce("Le Barman", message, Instant.now());
    }
}