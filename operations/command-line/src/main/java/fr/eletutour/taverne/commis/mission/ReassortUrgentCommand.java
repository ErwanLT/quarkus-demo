package fr.eletutour.taverne.commis.mission;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

/**
 * Mission : passer une commande de reassort d'urgence au marche.
 *
 * <p>Squelette a completer : injecter ici un service de commande
 * (appel a une API du marche, ecriture dans un fichier de suivi, etc.)</p>
 */
@Command(
        name = "reassort-urgent",
        description = "Passe une commande de reassort d'urgence au marche."
)
public class ReassortUrgentCommand implements Callable<Integer> {

    @Parameters(
            index = "0",
            description = "Nom de la denree a reapprovisionner (ex : houblon, whisky-naine)."
    )
    String denree;

    @Parameters(
            index = "1",
            description = "Quantite a commander."
    )
    int quantite;

    @Override
    public Integer call() {
        // TODO : brancher ici le vrai service de commande au marche.
        System.out.printf("Commande d'urgence enregistree : %d unite(s) de %s.%n", quantite, denree);
        return 0;
    }
}
