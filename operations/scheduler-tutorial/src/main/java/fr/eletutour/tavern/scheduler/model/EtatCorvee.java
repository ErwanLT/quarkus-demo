package fr.eletutour.tavern.scheduler.model;

import java.time.Instant;

/**
 * Dernier passage d'une corvée. {@code planifiee} et {@code reelle} permettent
 * de voir si un déclenchement a été retardé (redémarrage, thread occupé, ...).
 */
public record EtatCorvee(
        String tache,
        long executions,
        Instant planifiee,
        Instant reelle,
        String fil) {
}
