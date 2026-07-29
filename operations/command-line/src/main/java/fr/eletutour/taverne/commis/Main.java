package fr.eletutour.taverne.commis;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Point d'entree du processus.
 *
 * <p>Delegue immediatement a {@link CommisShellApplication}, qui garde le commis
 * eveille dans une boucle de lecture des commandes plutot que de l'eteindre
 * apres une seule execution.</p>
 */
@QuarkusMain
public class Main {

    public static void main(String... args) {
        Quarkus.run(CommisShellApplication.class, args);
    }
}
