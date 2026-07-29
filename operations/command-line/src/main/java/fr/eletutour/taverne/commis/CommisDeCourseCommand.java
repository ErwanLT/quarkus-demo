package fr.eletutour.taverne.commis;

import fr.eletutour.taverne.commis.mission.ExporterDettesCommand;
import fr.eletutour.taverne.commis.mission.PurgerGrimoireCommand;
import fr.eletutour.taverne.commis.mission.ReassortUrgentCommand;
import picocli.CommandLine.Command;

/**
 * Point d'entree du commis de course.
 *
 * <p>Contrairement au serveur au comptoir (REST/GraphQL) qui reste eveille toute la journee,
 * ou au brasseur de nuit (Jakarta Batch) qui travaille jusqu'a l'aube, le commis n'est reveille
 * que pour une mission ultra-precise. Il s'execute, rend son rapport, et repart aussitot
 * se coucher : pas de contexte HTTP, pas de scheduler, juste une tache et sa sortie.</p>
 *
 * <p>Chaque mission est une sous-commande independante, injectable et testable
 * separement du reste de l'application.</p>
 */
@Command(
        name = "commis",
        mixinStandardHelpOptions = true,
        version = "commis-de-course 1.0.0",
        description = "Le commis de course de la taverne : reveille pour une mission, puis reparti se coucher.",
        subcommands = {
                ExporterDettesCommand.class,
                ReassortUrgentCommand.class,
                PurgerGrimoireCommand.class
        }
)
public class CommisDeCourseCommand implements Runnable {

    @Override
    public void run() {
        // Aucune mission precisee : le commis rappelle simplement qu'il attend des ordres.
        System.out.println("Le commis attend une mission. Utilisez --help pour voir la liste des courses disponibles.");
    }
}
