package fr.eletutour.tavern.flammes.sante;

import java.time.Instant;

/**
 * Une ligne de la chronologie de l'incident, telle qu'elle sera reprise dans le post-mortem.
 */
public record EvenementIncendie(Instant horodatage, String etape, String detail) {
}
