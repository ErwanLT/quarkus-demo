package fr.eletutour.tavern.flammes.cuisine;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * La cuisine reactive : on ne laisse pas un aventurier attendre son plat au milieu des flammes.
 *
 * <p>Chaque commande est bornee par {@code taverne.cuisine.timeout}. Passe ce delai, la chaine
 * Mutiny echoue puis se rattrape sur un repas de secours : l'aventurier repart avec quelque
 * chose et, surtout, le thread est rendu au pool.</p>
 */
@ApplicationScoped
public class CuisineService {

    private static final Logger LOG = Logger.getLogger(CuisineService.class);

    private final AtomicLong platsServis = new AtomicLong();
    private final AtomicLong repasDeSecours = new AtomicLong();

    @ConfigProperty(name = "taverne.cuisine.timeout", defaultValue = "2s")
    Duration timeoutCuisine;

    public Duration timeoutCuisine() {
        return timeoutCuisine;
    }

    public long platsServis() {
        return platsServis.get();
    }

    public long repasDeSecours() {
        return repasDeSecours.get();
    }

    public void reinitialiserCompteurs() {
        platsServis.set(0L);
        repasDeSecours.set(0L);
    }

    public Uni<Repas> commanderRepas(String plat, long dureePreparationMs) {
        String platDemande = (plat == null || plat.isBlank()) ? "ragout de sanglier" : plat;

        return preparerPlatAsynchrone(platDemande, dureePreparationMs)
            .ifNoItem().after(timeoutCuisine)
            .failWith(() -> new TimeoutException("La cuisine est surchargee !"))
            .onFailure().invoke(echec ->
                LOG.warnf("Commande abandonnee : plat=%s, timeout=%sms, cause=%s",
                    platDemande, timeoutCuisine.toMillis(), echec.getMessage()))
            .onFailure().recoverWithItem(() -> Repas.secours(platDemande))
            .onItem().invoke(repas -> {
                if (Repas.STATUT_SECOURS.equals(repas.statut())) {
                    repasDeSecours.incrementAndGet();
                } else {
                    platsServis.incrementAndGet();
                }
            });
    }

    /**
     * Simule la preparation en cuisine : plus l'incendie progresse, plus le plat tarde.
     */
    private Uni<Repas> preparerPlatAsynchrone(String plat, long dureePreparationMs) {
        return Uni.createFrom().item(() -> Repas.servi(plat))
            .onItem().delayIt().by(Duration.ofMillis(Math.max(0L, dureePreparationMs)));
    }
}
