package fr.eletutour.taverne.commis.mission;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * Mission : purger un vieux grimoire devenu illisible ou obsolete.
 *
 * <p>Squelette a completer : injecter ici un service d'acces au grimoire
 * (fichier, base documentaire, etc.) et definir la logique de purge.</p>
 */
@Command(
        name = "purger-grimoire",
        description = "Purge les entrees perimees d'un grimoire."
)
public class PurgerGrimoireCommand implements Callable<Integer> {

    @Option(
            names = {"-n", "--nom"},
            description = "Nom du grimoire a purger.",
            required = true
    )
    String nomGrimoire;

    @Option(
            names = {"--simulation"},
            description = "N'affiche que ce qui serait purge, sans rien modifier."
    )
    boolean simulation;

    @Override
    public Integer call() {
        // TODO : brancher ici la vraie logique de purge du grimoire.
        String mode = simulation ? "simulation" : "purge effective";
        System.out.printf("Grimoire '%s' traite en mode %s.%n", nomGrimoire, mode);
        return 0;
    }
}
