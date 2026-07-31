package fr.eletutour.taverne.commis.mission;

import fr.eletutour.taverne.commis.affichage.DemandeConfirmation;
import jakarta.inject.Inject;
import org.jline.terminal.Terminal;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * Mission : purger un vieux grimoire devenu illisible ou obsolete.
 *
 * <p>Une purge reelle est irreversible : sauf en mode simulation, le commis
 * demande donc confirmation avant de proceder.</p>
 */
@Command(
        name = "purger-grimoire",
        description = "Purge les entrees perimees d'un grimoire."
)
public class PurgerGrimoireCommand implements Callable<Integer> {

    @Inject
    Terminal terminal;

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
        if (!simulation) {
            DemandeConfirmation confirmation = new DemandeConfirmation(terminal);
            boolean accepte = confirmation.confirmer(
                    "Purger definitivement '" + nomGrimoire + "' ? Cette action est irreversible.");

            if (!accepte) {
                System.out.println("Purge annulee. Le grimoire reste intact.");
                return 0;
            }
        }

        // TODO : brancher ici la vraie logique de purge du grimoire.
        String mode = simulation ? "simulation" : "purge effective";
        System.out.printf("Grimoire '%s' traite en mode %s.%n", nomGrimoire, mode);
        return 0;
    }
}
