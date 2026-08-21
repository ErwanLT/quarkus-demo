package fr.eletutour.tavern.flammes.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
@DisplayName("Le grimoire distant est protege par @Timeout, @Retry et @Fallback")
class GrimoireResourceTest {

    @BeforeEach
    @AfterEach
    void remettreLeGrimoireDAplomb() {
        given().queryParam("active", false).when().put("/taverne/grimoire/pannes").then().statusCode(200);
        given().queryParam("ms", 10).when().put("/taverne/grimoire/latences").then().statusCode(200);
    }

    @Test
    @DisplayName("retourne la recette du grimoire en un seul appel quand tout va bien")
    void doitRetournerLaRecetteDuGrimoire() {
        given()
            .queryParam("nom", "tourte aux navets")
            .when().get("/taverne/grimoire/recettes")
            .then()
            .statusCode(200)
            .body("nom", equalTo("tourte aux navets"))
            .body("origine", equalTo("GRIMOIRE"))
            .body("tentatives", equalTo(1))
            .body("texte", startsWith("Recette de tourte aux navets"));
    }

    @Test
    @DisplayName("retente une fois puis sort la recette de memoire quand le grimoire est en panne")
    void doitRetenterPuisBasculerSurLaRecetteDeMemoire() {
        given().queryParam("active", true).when().put("/taverne/grimoire/pannes").then().statusCode(200);

        given()
            .queryParam("nom", "ragout de sanglier")
            .when().get("/taverne/grimoire/recettes")
            .then()
            .statusCode(200)
            .body("origine", equalTo("MEMOIRE_DU_CHEF"))
            .body("tentatives", equalTo(2))
            .body("texte", startsWith("De memoire : ragout de sanglier"));
    }

    @Test
    @DisplayName("bascule sur le fallback quand le grimoire depasse le timeout")
    void doitBasculerSurLeFallbackApresLeTimeout() {
        given().queryParam("ms", 3_000).when().put("/taverne/grimoire/latences").then().statusCode(200);

        given()
            .queryParam("nom", "soupe de racines")
            .when().get("/taverne/grimoire/recettes")
            .then()
            .statusCode(200)
            .body("origine", equalTo("MEMOIRE_DU_CHEF"));
    }
}
