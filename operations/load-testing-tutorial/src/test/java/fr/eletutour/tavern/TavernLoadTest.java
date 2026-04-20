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

    // --- Configuration ---
    private static final String BASE_URL = "http://localhost:8080";
    private static final String PATH_COMMANDE = "/taverne/commande";
    private static final String PATH_CAVE = "/taverne/cave";
    private static final String PATH_PLAT = "/taverne/plat-du-jour";

    // --- Paramètres ---
    private static final int NB_REGULIERS = 200;
    private static final int NB_BURST = 80;
    private static final int NB_AFFAMES = 150;

    HttpProtocolBuilder httpProtocol = http
        .baseUrl(BASE_URL)
        .acceptHeader("text/plain")
        .userAgentHeader("Gatling/Tavern-Resilience-Test");

    // =========================================================
    // 🍺 SCENARIO 1 : Consommation réaliste (parcours complet)
    // =========================================================
    ScenarioBuilder parcoursClassique = scenario("Parcours classique d’un aventurier")
        // --- Bière (rate limit possible) ---
        .exec(
            http("Commande de bière")
                .post(PATH_COMMANDE)
                .check(status().is(200))
        )
        .pause(1, 3)

        // Si rate limit → on arrête ici (logique métier)
        .exitHereIfFailed()

        // --- Cave (retry attendu → temps > 200ms) ---
        .exec(
            http("Reset cave")
                .get(PATH_CAVE)
                .queryParam("reset", "true")
                .check(status().is(200))
        )
        .pause(1, 2)
        .exec(
            http("Descente à la cave avec retry")
                .get(PATH_CAVE)
                .check(status().is(200))
        )
        .pause(1, 3)

        // --- Plat normal ---
        .exec(
            http("Plat du jour (normal)")
                .get(PATH_PLAT)
                .queryParam("empty", "false")
                .check(status().is(200))
                .check(bodyString().is("Voici un délicieux ragoût de sanglier !"))
        );

    // =========================================================
    // ⚒️ SCENARIO 2 : Burst → test du Rate Limiting
    // =========================================================
    ScenarioBuilder burstDeNains = scenario("Burst de nains assoiffés")
        .exec(
            http("Commande massive de bière")
                .post(PATH_COMMANDE)
                .check(status().is(200))
        );

    // =========================================================
    // 🍞 SCENARIO 3 : Fallback (rupture de stock)
    // =========================================================
    ScenarioBuilder affames = scenario("Affamés face à la rupture")
        .exec(
            http("Plat du jour vide → fallback")
                .get(PATH_PLAT)
                .queryParam("empty", "true")
                .check(status().is(200))
                .check(bodyString().is(
                        "Désolé l'ami, la marmite est vide... Tiens, un morceau de pain dur et du fromage sec en lot de consolation."
                ))
        );

    // =========================================================
    // 🪜 SCENARIO 4 : Test pur du Retry (isolé)
    // =========================================================
    ScenarioBuilder testRetry = scenario("Test isolé du retry")
        .exec(
            http("Reset cave")
                .get(PATH_CAVE)
                .queryParam("reset", "true")
                .check(status().is(200))
        )
        .pause(1)
        .exec(
            http("Retry complet")
                .get(PATH_CAVE)
                .check(status().is(200))
        );

    // =========================================================
    // ⚙️ SETUP GLOBAL
    // =========================================================
    {
        setUp(
            // Trafic réaliste
            parcoursClassique.injectOpen(
                    rampUsers(NB_REGULIERS).during(30)
            ),
            // Burst violent
            burstDeNains.injectOpen(
                    nothingFor(5),
                    atOnceUsers(1000)
            ),
            // Fallback sous pression
            affames.injectOpen(
                    rampUsers(NB_AFFAMES).during(20)
            ),
            // Retry isolé
            testRetry.injectOpen(
                    constantUsersPerSec(5).during(20)
            )
        )
            .protocols(httpProtocol)

            // =====================================================
            // 📊 ASSERTIONS (SLA)
            // =====================================================
                .assertions(
                    // Global : performance générale
                    global().responseTime().percentile3().lt(500),

                    // Global : Avec 1000 nains rejetés sur 1500 requêtes, le max théorique est ~33%
                    // On s'attend donc à au moins 15% de succès globaux pour les autres
                    global().successfulRequests().percent().gt(15.0),

                    // Rate limiting : Le burst doit être rejeté massivement (> 95%)
                    details("Commande massive de bière")
                            .failedRequests().percent().gt(95.0),

                    // Retry : Le 95ème percentile (percentile3 dans Gatling) doit prouver le délai du retry
                    details("Retry complet")
                            .responseTime().percentile3().gt(200),

                    // Fallback : jamais d’échec
                    details("Plat du jour vide → fallback")
                            .failedRequests().count().is(0L)
                );
    }
}
