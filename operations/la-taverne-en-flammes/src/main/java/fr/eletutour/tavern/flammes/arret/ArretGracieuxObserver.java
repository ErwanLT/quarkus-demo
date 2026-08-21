package fr.eletutour.tavern.flammes.arret;

import fr.eletutour.tavern.flammes.corbeaux.VoliereService;
import fr.eletutour.tavern.flammes.sante.IncendieService;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;

/**
 * Le tavernier qui verrouille les portes.
 *
 * <p>A la reception du {@code SIGTERM}, Quarkus arrete d'accepter de nouvelles requetes puis
 * publie un {@link ShutdownEvent}. On en profite pour tracer ce qui reste en vol : c'est cette
 * trace qui permet, apres coup, de verifier que le delai accorde par
 * {@code quarkus.shutdown.timeout} etait suffisant.</p>
 */
@ApplicationScoped
public class ArretGracieuxObserver {

    private static final Logger LOG = Logger.getLogger(ArretGracieuxObserver.class);

    @Inject
    SalleCommuneService salleCommuneService;

    @Inject
    VoliereService voliereService;

    @Inject
    IncendieService incendieService;

    @ConfigProperty(name = "quarkus.shutdown.timeout")
    Duration delaiArret;

    @ConfigProperty(name = "taverne.voliere.vidange-max", defaultValue = "10s")
    Duration delaiVidange;

    void auDemarrage(@Observes StartupEvent evenement) {
        LOG.infof("La taverne ouvre ses portes. Delai d'arret gracieux accorde : %ss", delaiArret.toSeconds());
    }

    void aLArret(@Observes ShutdownEvent evenement) {
        int tournees = salleCommuneService.tourneesEnCours();
        long corbeauxEnVol = voliereService.etat().corbeauxEnVol();

        incendieService.noter("ARRET_GRACIEUX", "Signal d'arret recu, les requetes en cours vont jusqu'au bout");
        LOG.infof("Fermeture des portes : tourneesEnCours=%d, corbeauxEnVol=%d, delaiAccorde=%ss",
            tournees, corbeauxEnVol, delaiArret.toSeconds());

        // Quarkus a deja attendu la fin des requetes HTTP. Les plis, eux, ne sont pas suivis par
        // le serveur HTTP : c'est ici qu'on retient l'arret le temps de les finir.
        long plisPerdus = voliereService.attendreVidange(delaiVidange);

        if (plisPerdus > 0) {
            incendieService.noter("VIDANGE_INCOMPLETE", plisPerdus + " plis perdus a l'arret");
            LOG.warnf("La taverne ferme avec %d plis non lus : allonger taverne.voliere.vidange-max "
                + "ou accelerer le traitement", plisPerdus);
        } else {
            LOG.info("Salle vide et voliere vidangee : la taverne peut fermer sereinement");
        }
    }
}
