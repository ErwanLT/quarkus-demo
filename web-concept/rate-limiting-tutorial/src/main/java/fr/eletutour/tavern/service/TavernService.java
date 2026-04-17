package fr.eletutour.tavern.service;

import io.smallrye.faulttolerance.api.RateLimit;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class TavernService {

    private static final Logger LOG = Logger.getLogger(TavernService.class);

    private final AtomicInteger cellarTrips = new AtomicInteger(0);

    @RateLimit(value = 3, window = 10, windowUnit = ChronoUnit.SECONDS)
    public String orderBeer() {
        return "Tiens, une bonne pinte bien fraîche !";
    }

    @Retry(maxRetries = 3, delay = 200)
    public String fetchFromCellar(boolean reset) {
        if (reset) {
            cellarTrips.set(0);
            return "Cave réinitialisée";
        }

        int attempt = cellarTrips.incrementAndGet();
        if (attempt <= 2) {
            LOG.warn("Oups ! Le tavernier a glissé sur une marche vers la cave ! (Tentative " + attempt + ")");
            throw new RuntimeException("Tavernier tombé dans les escaliers");
        }
        
        LOG.info("Victoire ! La bouteille est remontée (Tentative " + attempt + ")");
        return "Voilà votre prestigieux Vin Elfique millésimé !";
    }

    @Fallback(fallbackMethod = "serveLeftovers")
    public String orderPlatDuJour(boolean empty) {
        if (empty) {
            throw new RuntimeException("Plus aucun ragoût de sanglier !");
        }
        return "Voici un délicieux ragoût de sanglier !";
    }

    public String serveLeftovers(boolean empty) {
        return "Désolé l'ami, la marmite est vide... Tiens, un morceau de pain dur et du fromage sec en lot de consolation.";
    }
}
