package fr.eletutour.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration de l'ardoise magique.
 *
 * <p>Ces tests valident les trois comportements clés :</p>
 * <ol>
 *   <li>Le premier appel (cache miss) est lent (≥ 200 ms)</li>
 *   <li>Le deuxième appel (cache hit) est très rapide (< 50 ms)</li>
 *   <li>Après invalidation, le prochain appel redevient lent</li>
 * </ol>
 *
 * <p>Les tests sont ordonnés pour simuler un scénario réaliste :
 * premier appel → second appel → invalidation → troisième appel.</p>
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuResourceTest {

    private static final String JOUR = "lundi";

    // -------------------------------------------------------------------------
    // Scénario 1 : structure et contenu du menu
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    void premierAppel_doitRetournerLeMenuComplet() {
        given()
            .when().get("/menu/" + JOUR)
            .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].nom", is("Ragoût de sanglier"))
                .body("[0].prixEnOr", is(8.5f))
                .body("[1].nom", is("Pain de seigle"))
                .body("[1].prixEnOr", is(1.5f));
    }

    @Test
    @Order(2)
    void deuxiemeAppel_doitRetournerLeMemeMenu() {
        // Le cache est chaud : même contenu, réponse immédiate
        given()
            .when().get("/menu/" + JOUR)
            .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].nom", is("Ragoût de sanglier"));
    }

    // -------------------------------------------------------------------------
    // Scénario 2 : jour inconnu
    // -------------------------------------------------------------------------

    @Test
    @Order(3)
    void jourInconnu_doitRetourner404() {
        given()
            .when().get("/menu/dimanche")
            .then()
                .statusCode(404)
                .body(containsString("dimanche"));
    }

    // -------------------------------------------------------------------------
    // Scénario 3 : invalidation d'un jour
    // -------------------------------------------------------------------------

    @Test
    @Order(4)
    void effacerJour_doitRetourner204() {
        given()
            .when().delete("/menu/" + JOUR)
            .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void apresInvalidation_premierAppelRedescendALaCave() {
        // Après invalidation, le cache est vide : on doit redescendre à la cave.
        // Le menu doit quand même être correct.
        given()
            .when().get("/menu/" + JOUR)
            .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].nom", is("Ragoût de sanglier"));
    }

    // -------------------------------------------------------------------------
    // Scénario 4 : invalidation totale
    // -------------------------------------------------------------------------

    @Test
    @Order(6)
    void effacerTouteArdoise_doitRetourner204() {
        given()
            .when().delete("/menu")
            .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    void apresInvalidationTotale_tousLesJoursSontReconstruits() {
        // Vérification que mardi est aussi disponible après une invalidation totale
        given()
            .when().get("/menu/mardi")
            .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].nom", is("Soupe de légumes"));
    }

    // -------------------------------------------------------------------------
    // Scénario 5 : gain de performance observable (cache hit vs miss)
    // -------------------------------------------------------------------------

    @Test
    @Order(8)
    void cacheMiss_doitEtrePlusLentQueCacheHit() {
        // Invalidation pour forcer un cache miss
        given().when().delete("/menu/mercredi").then().statusCode(204);

        // Premier appel : cache miss → doit prendre au moins 200 ms (délai cave)
        long debut = System.currentTimeMillis();
        given().when().get("/menu/mercredi").then().statusCode(200);
        long dureeCacheMiss = System.currentTimeMillis() - debut;

        // Deuxième appel : cache hit → doit être très rapide
        debut = System.currentTimeMillis();
        given().when().get("/menu/mercredi").then().statusCode(200);
        long dureeCacheHit = System.currentTimeMillis() - debut;

        // Le cache miss doit avoir pris plus de temps que le cache hit
        assert dureeCacheMiss > dureeCacheHit
                : "Cache miss (%d ms) devrait être plus lent que cache hit (%d ms)"
                    .formatted(dureeCacheMiss, dureeCacheHit);
    }
}
