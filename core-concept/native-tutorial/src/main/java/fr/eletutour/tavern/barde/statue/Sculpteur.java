package fr.eletutour.tavern.barde.statue;

import java.time.Instant;

/**
 * Le sculpteur grave la date dans le socle de la statue.
 *
 * <p>Le champ est {@code static final} : il est calculé une seule fois, à l'initialisation
 * de la classe. Sur la JVM, cela se passe au démarrage. En natif, si la classe est
 * initialisée au build, la valeur est figée dans le binaire : la statue affiche alors
 * la date de la forge, et non celle de son réveil.</p>
 */
public final class Sculpteur {

    public static final Instant DATE_GRAVEE = Instant.now();

    private Sculpteur() {
    }
}
