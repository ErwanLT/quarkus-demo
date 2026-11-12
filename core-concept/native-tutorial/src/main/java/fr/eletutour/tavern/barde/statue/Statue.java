package fr.eletutour.tavern.barde.statue;

import java.time.Instant;

/**
 * L'état de la statue du barde.
 *
 * @param mode        "natif" ou "jvm"
 * @param dateGravee  la date gravée dans le socle (champ statique du {@link Sculpteur})
 * @param reveil      l'instant où l'application a démarré
 */
public record Statue(String mode, Instant dateGravee, Instant reveil) {
}
