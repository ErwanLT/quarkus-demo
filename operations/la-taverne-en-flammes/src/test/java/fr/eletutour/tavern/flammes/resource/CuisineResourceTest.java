package fr.eletutour.tavern.flammes.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@DisplayName("La commande reactive se rattrape sur un repas de secours")
class CuisineResourceTest {

    @Test
    @DisplayName("sert le plat quand la cuisine repond dans le delai")
    void doitServirLePlatDansLeDelai() {
        given()
            .queryParam("plat", "ragout de sanglier")
            .queryParam("preparationMs", 20)
            .when().get("/taverne/cuisine/commandes")
            .then()
            .statusCode(200)
            .body("plat", equalTo("ragout de sanglier"))
            .body("statut", equalTo("SERVI"));
    }

    @Test
    @DisplayName("sert du pain et de l'eau quand la cuisine depasse le timeout")
    void doitServirDuPainEtDeLEauApresLeTimeout() {
        long timeoutMs = given()
            .when().get("/taverne/cuisine/timeout")
            .then()
            .statusCode(200)
            .extract().as(Long.class);

        given()
            .queryParam("plat", "cuissot de dragon")
            .queryParam("preparationMs", timeoutMs * 3)
            .when().get("/taverne/cuisine/commandes")
            .then()
            .statusCode(200)
            .body("plat", equalTo("Pain et Eau"))
            .body("statut", equalTo("SECOURS"));
    }
}
