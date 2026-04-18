package fr.eletutour.tavern.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

/**
 * Tests d'intégration pour les routes REST de la Taverne, validant 
 * l'implémentation de la tolérance aux pannes (Fault Tolerance).
 */
@QuarkusTest
class TavernResourceTest {

    @Test
    @DisplayName("Devrait accepter les 3 premières commandes et rejeter la 4ème (Rate Limiting)")
    void testRateLimiting() {
        for (int i = 0; i < 3; i++) {
            RestAssured.given()
                    .when().post("/taverne/commande")
                    .then()
                    .statusCode(200)
                    .body(equalTo("Tiens, une bonne pinte bien fraîche !"));
        }

        RestAssured.given()
                .when().post("/taverne/commande")
                .then()
                .statusCode(429)
                .body(equalTo("Holà l'ami ! Laisse-moi le temps de tirer ta bière, le tonneau n'est pas infini !"));
    }

    @Test
    @DisplayName("Devrait réussir à remonter la bouteille de la cave au bout de 3 tentatives (Retry)")
    void testRetry() {
        RestAssured.given()
                .queryParam("reset", true)
                .when().get("/taverne/cave")
                .then().statusCode(200);

        RestAssured.given()
                .when().get("/taverne/cave")
                .then()
                .statusCode(200)
                .body(equalTo("Voilà votre prestigieux Vin Elfique millésimé !"));
    }

    @Test
    @DisplayName("Devrait servir le plat du jour nominalement quand tout va bien")
    void testFallbackSuccess() {
        RestAssured.given()
                .queryParam("empty", false)
                .when().get("/taverne/plat-du-jour")
                .then()
                .statusCode(200)
                .body(equalTo("Voici un délicieux ragoût de sanglier !"));
    }

    @Test
    @DisplayName("Devrait servir une solution de secours suite à une rupture de stock (Fallback)")
    void testFallbackFailure() {
        RestAssured.given()
                .queryParam("empty", true)
                .when().get("/taverne/plat-du-jour")
                .then()
                .statusCode(200)
                .body(equalTo("Désolé l'ami, la marmite est vide... Tiens, un morceau de pain dur et du fromage sec en lot de consolation."));
    }
}
