package fr.eletutour.taverne.commis.mission;

import fr.eletutour.taverne.commis.domain.DetteAventurier;
import fr.eletutour.taverne.commis.service.RegistreDettesService;
import jakarta.inject.Inject;
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

        dettes.forEach(dette -> System.out.printf(
                "%-12s %8.2f po (%d jours de retard)%s%n",
                dette.nomAventurier(),
                dette.montant(),
                dette.joursDeRetard(),
                dette.estCritique() ? "  [CRITIQUE]" : ""
        ));

        System.out.printf("%nMission accomplie : %d dette(s) rapportee(s).%n", dettes.size());
        return 0;
    }
}
