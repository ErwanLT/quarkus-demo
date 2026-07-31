package fr.eletutour.taverne.commis.mission;

import fr.eletutour.taverne.commis.affichage.GrandRegistre;
import fr.eletutour.taverne.commis.affichage.SceauDuTavernier;
import fr.eletutour.taverne.commis.domain.DetteAventurier;
import fr.eletutour.taverne.commis.service.RegistreDettesService;
import jakarta.inject.Inject;
import org.jline.terminal.Terminal;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Mission : dresser et exporter la liste des dettes des aventuriers.
 *
 * <p>Convoque quand le tavernier veut savoir qui lui doit encore de l'or,
 * sans attendre le prochain inventaire de nuit du brasseur.</p>
 */
@Command(
        name = "exporter-dettes",
        description = "Dresse la liste des dettes des aventuriers de la taverne."
)
public class ExporterDettesCommand implements Callable<Integer> {

    @Inject
    RegistreDettesService registreDettesService;

    @Inject
    Terminal terminal;

    @Option(
            names = {"-c", "--critiques-seulement"},
            description = "N'affiche que les dettes critiques (retard superieur a 30 jours)."
    )
    boolean critiquesSeulement;

    @Override
    public Integer call() {
        List<DetteAventurier> dettes = registreDettesService.listerDettes().stream()
                .filter(dette -> !critiquesSeulement || dette.estCritique())
                .toList();

        if (dettes.isEmpty()) {
            System.out.println("Aucune dette a rapporter. La taverne est en paix.");
            return 0;
        }

        GrandRegistre registre = new GrandRegistre(List.of("Aventurier", "Montant", "Retard"));
        dettes.forEach(dette -> registre.ajouterLigne(List.of(
                dette.nomAventurier(),
                String.format("%.2f po", dette.montant()),
                dette.joursDeRetard() + " jours"
        )));

        System.out.println(registre.construireEntete());

        List<String> lignes = registre.construireLignes();
        for (int index = 0; index < dettes.size(); index++) {
            String ligne = lignes.get(index);
            if (dettes.get(index).estCritique()) {
                System.out.println(SceauDuTavernier.alerte(terminal, ligne));
            } else {
                System.out.println(ligne);
            }
        }

        System.out.println(registre.construirePied());
        System.out.printf("%nMission accomplie : %d dette(s) rapportee(s).%n", dettes.size());
        return 0;
    }
}
