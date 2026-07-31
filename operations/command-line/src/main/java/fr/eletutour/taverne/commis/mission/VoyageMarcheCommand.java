package fr.eletutour.taverne.commis.mission;

import fr.eletutour.taverne.commis.affichage.JaugeDeTonneau;
import fr.eletutour.taverne.commis.affichage.Sablier;
import jakarta.inject.Inject;
import org.jline.terminal.Terminal;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * Mission un peu plus longue que les autres : le commis part au marche,
 * patiente le temps du trajet (sablier), puis charge sa carriole de
 * provisions (jauge de tonneau) avant de rentrer.
 *
 * <p>Sert surtout de terrain de demonstration pour les composants d'affichage
 * du package {@code affichage}, la ou {@code exporter-dettes} se contentait
 * d'un rapport instantane.</p>
 */
@Command(
        name = "voyage-marche",
        description = "Envoie le commis au marche chercher une carriole de provisions."
)
public class VoyageMarcheCommand implements Callable<Integer> {

    @Inject
    Terminal terminal;

    @Override
    public Integer call() throws Exception {
        Sablier sablier = new Sablier(terminal);
        sablier.tourner("Le commis trotte jusqu'au marche...", () -> {
            Thread.sleep(1500);
            return null;
        });

        JaugeDeTonneau jauge = new JaugeDeTonneau(terminal, "Chargement de la carriole");
        for (int pourcentage = 0; pourcentage <= 100; pourcentage += 4) {
            jauge.avancerA(pourcentage);
            Thread.sleep(100);
        }
        jauge.terminer();

        System.out.println("La carriole est chargee. Retour a la taverne.");
        return 0;
    }
}
