package fr.eletutour.tavern.flammes.corbeaux;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Le colombier : il lit les plis un par un, volontairement lentement.
 *
 * <p>Traiter lentement permet de constituer un retard visible ({@code corbeauxEnVol}) puis
 * d'observer, lors d'un {@code SIGTERM}, que le lot deja recupere est finalise avant l'arret au
 * lieu d'etre perdu.</p>
 */
@ApplicationScoped
public class ColombierListener {

    private static final Logger LOG = Logger.getLogger(ColombierListener.class);

    @Inject
    VoliereService voliereService;

    @ConfigProperty(name = "taverne.voliere.traitement-ms", defaultValue = "200")
    long dureeTraitementMs;

    @Incoming("corbeaux-en-vol")
    @Blocking
    void recevoir(Corbeau corbeau) {
        attendre();
        voliereService.marquerTraite(corbeau);
    }

    private void attendre() {
        try {
            Thread.sleep(Math.max(0L, dureeTraitementMs));
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            LOG.warn("Lecture des plis interrompue : le corbeau sera relu au prochain demarrage");
        }
    }
}
