package fr.eletutour.database.migrations.registre;

import java.time.Instant;

public record Travail(String version, String description, String auteur, Instant executeLe, String etat) {
}
