package fr.eletutour.tavern.service;

import io.smallrye.faulttolerance.api.RateLimit;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service modélisant les actions du tavernier au sein d'une taverne médiévale de type DnD.
 * <p>
 * Ce service gère la logique métier des commandes et de l'approvisionnement tout en
 * encapsulant les principes de résilience (Rate Limiting, Retry, Fallback).
 * </p>
 */
@ApplicationScoped
public class TavernService {

    private static final Logger LOG = Logger.getLogger(TavernService.class);

    private final AtomicInteger cellarTrips = new AtomicInteger(0);

    /**
     * Commande une bière au comptoir.
     * <p>
     * Le tavernier ne peut servir qu'un nombre limité de verres pour éviter l'épuisement.
     * Protégé par une limite de appels de 3 fois toutes les 10 secondes.
     * </p>
     *
     * @return le message de confirmation du tavernier
     * @throws io.smallrye.faulttolerance.api.RateLimitException si la limite est dépassée
     */
    @RateLimit(value = 3, window = 10, windowUnit = ChronoUnit.SECONDS)
    public String orderBeer() {
        return "Tiens, une bonne pinte bien fraîche !";
    }

    /**
     * Demande au tavernier d'aller chercher une bouteille à la cave.
     * <p>
     * Le tavernier a tendance à trébucher dans les escaliers. Cette méthode échoue
     * intentionnellement lors des 2 premières tentatives pour démontrer le mécanisme de {@link Retry}.
     * </p>
     *
     * @return la bouteille de vin elfique après les éventuelles chutes
     * @throws RuntimeException durant les deux premières tentatives simulées (rattrapé par le Retry)
     */
    @Retry(maxRetries = 3, delay = 200)
    public String fetchFromCellar() {
        // 60% de chance de trébucher à chaque tentative, indépendant des autres requêtes
        if (Math.random() < 0.60) {
            LOG.warn("Oups ! Le tavernier a glissé sur une marche vers la cave !");
            throw new RuntimeException("Tavernier tombé dans les escaliers");
        }

        LOG.info("Victoire ! La bouteille est remontée.");
        return "Voilà votre prestigieux Vin Elfique millésimé !";
    }

    /**
     * Commande le plat du jour à la taverne.
     * <p>
     * S'il n'y a plus de plat (simulé par le paramètre {@code empty}), déclenche un comportement de secours
     * (Fallback) au lieu de faire échouer la commande dramatiquement.
     * </p>
     *
     * @param empty indique si la marmite est supposée être vide
     * @return la description du plat du jour, ou une alternative via le Fallback
     * @throws RuntimeException si la marmite est vide (déclenche le Fallback)
     */
    @Fallback(fallbackMethod = "serveLeftovers")
    public String orderPlatDuJour(boolean empty) {
        if (empty) {
            throw new RuntimeException("Plus aucun ragoût de sanglier !");
        }
        return "Voici un délicieux ragoût de sanglier !";
    }

    /**
     * Méthode de secours (Fallback) pour le plat du jour.
     * <p>
     * Doit avoir la même signature que la méthode originelle (ou prendre l'exception en paramètre).
     * Sert une alternative aux aventuriers lorsque le plat principal est épuisé.
     * </p>
     *
     * @param empty le paramètre de la requête d'origine (ignoré ici)
     * @return la consolation culinaire de secours
     */
    public String serveLeftovers(boolean empty) {
        return "Désolé l'ami, la marmite est vide... Tiens, un morceau de pain dur et du fromage sec en lot de consolation.";
    }
}
