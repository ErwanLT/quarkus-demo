package fr.eletutour.tavern.flammes.arret;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * La salle commune : elle sert des tournees volontairement lentes.
 *
 * <p>C'est le support de la demonstration d'arret gracieux. Lancez une tournee de 20 secondes,
 * envoyez un {@code SIGTERM} pendant le service, et observez que Quarkus refuse les nouvelles
 * requetes mais laisse celle-ci se terminer dans la limite de
 * {@code quarkus.shutdown.timeout}.</p>
 */
@ApplicationScoped
public class SalleCommuneService {

    private static final Logger LOG = Logger.getLogger(SalleCommuneService.class);
    private static final int DUREE_MAX_SECONDES = 60;

    private final AtomicInteger tourneesEnCours = new AtomicInteger();

    public int tourneesEnCours() {
        return tourneesEnCours.get();
    }

    public TourneeServie servirTournee(String aventurier, int secondes) {
        String client = (aventurier == null || aventurier.isBlank()) ? "aventurier anonyme" : aventurier;
        Duration duree = Duration.ofSeconds(Math.clamp(secondes, 0, DUREE_MAX_SECONDES));

        int enCours = tourneesEnCours.incrementAndGet();
        LOG.infof("Debut de service : aventurier=%s, duree=%ss, tourneesEnCours=%d", client, duree.toSeconds(), enCours);
        try {
            attendre(duree);
            LOG.infof("Tournee servie jusqu'au bout : aventurier=%s", client);
            return TourneeServie.de(client, duree, tourneesEnCours.get() - 1);
        } finally {
            tourneesEnCours.decrementAndGet();
        }
    }

    private void attendre(Duration duree) {
        try {
            Thread.sleep(duree.toMillis());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Service interrompu avant la fin de la tournee", interruptedException);
        }
    }
}
