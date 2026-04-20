package fr.eletutour.tavern;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * Simulation de charge Gatling pour la Taverne Médiévale.
 * <p>
 * Elle permet de vérifier la robustesse de l'application Quarkus face à différents profils d'aventuriers
 * et de valider les mécanismes de résilience (Rate Limiting, Retry, Fallback).
 * </p>
 */
public class TavernLoadTest extends Simulation {

    // --- Configuration de la Taverne ---
    private static final String BASE_URL = "http://localhost:8080";
    private static final String PATH_COMMANDE = "/taverne/commande";
    private static final String PATH_CAVE = "/taverne/cave";
    private static final String PATH_PLAT = "/taverne/plat-du-jour";

    // --- Paramètres de charge ---
    private static final int NB_AVENTURIERS_REGULIERS = 300;
    private static final int DUREE_RAMP_REGULIERS = 30;

    private static final int NB_NAINS_BURST = 50;
    private static final int DELAI_BURST = 5;

    private static final int NB_AVENTURIERS_AFFAMES = 150;
    private static final int DUREE_RAMP_AFFAMES = 20;

    /** Protocol HTTP partagé par tous les scénarios de la taverne. */
    HttpProtocolBuilder httpProtocol = http
        .baseUrl(BASE_URL)
        .acceptHeader("text/plain")
        .userAgentHeader("Gatling/MedievalAdventurer");

    /** 
     * Scénario simulant une consommation normale. 
     * <p>Un aventurier commande une bière, demande du vin de la cave, puis mange le plat du jour.</p>
     */
    ScenarioBuilder aventuriersReguliers = scenario("Scénario : Consommation paisible au comptoir")
        .exec(http("Commande de bière (Standard)")
            .post(PATH_COMMANDE)
            .check(status().is(200)))
        .pause(1)
        .exec(http("Descente à la cave pour du Vin Elfique")
            .get(PATH_CAVE)
            .check(status().is(200)))
        .pause(1)
        .exec(http("Dégustation du Ragoût de Sanglier")
            .get(PATH_PLAT)
            .check(status().is(200)));

    /** 
     * Scénario simulant une arrivée massive et désordonnée.
     * <p>Utilisé pour saturer le Rate Limiting (429 attendus s'affichant en KO).</p>
     */
    ScenarioBuilder groupeDeNainsBurst = scenario("Scénario : Invasion de nains assoiffés")
        .exec(http("Commande de bière frénétique (Burst)")
            .post(PATH_COMMANDE)
            .check(status().is(200)));

    /** 
     * Scénario simulant une pénurie de ressources.
     * <p>Utilisé pour vérifier que le Fallback sert bien le repas de secours (200 OK).</p>
     */
    ScenarioBuilder aventuriersAffames = scenario("Scénario : Les affamés face à la marmite vide")
        .exec(http("Commande du plat du jour (Rupture simulée)")
            .get(PATH_PLAT)
            .queryParam("empty", "true")
            .check(status().is(200)));

    /**
     * Bloc d'initialisation de la simulation.
     * <p>Configure l'injection des utilisateurs et définit les assertions de performance (SLA).</p>
     */
    {
        setUp(
            aventuriersReguliers.injectOpen(
                rampUsers(NB_AVENTURIERS_REGULIERS).during(DUREE_RAMP_REGULIERS)
            ),
            groupeDeNainsBurst.injectOpen(
                nothingFor(DELAI_BURST),
                atOnceUsers(NB_NAINS_BURST)
            ),
            aventuriersAffames.injectOpen(
                rampUsers(NB_AVENTURIERS_AFFAMES).during(DUREE_RAMP_AFFAMES)
            )
        ).protocols(httpProtocol)
         .assertions(
             // SLA : Globalement, 95% des requêtes doivent répondre en moins de 100ms
             global().responseTime().percentile3().lt(100),
             // SLA : On tolère les KO (dues au rate limit), mais on veut un taux de réussite mini de 60%
             global().successfulRequests().percent().gt(60.0)
         );
    }
}
