package fr.eletutour.tavern.reactive.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class ReactiveTavernResourceTest {

    @Test
    @DisplayName("Devrait exposer une chope servie plus tard via Uni")
    void shouldExposeFutureBeerWithUni() {
        RestAssured.given()
                .queryParam("durationMs", 10)
                .when()
                .get("/taverne/reactif/pression/Gimli")
                .then()
                .statusCode(200)
                .body("adventurer", equalTo("Gimli"))
                .body("durationMs", equalTo(10))
                .body("message", containsString("chope est pleine"));
    }

    @Test
    @DisplayName("Devrait collecter le flux Multi dans une liste JSON")
    void shouldCollectMultiAsJsonList() {
        RestAssured.given()
                .queryParam("count", 3)
                .queryParam("intervalMs", 10)
                .when()
                .get("/taverne/reactif/tournee")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("[0].sequence", equalTo(1))
                .body("[2].client", equalTo("Client 3"));
    }

    @Test
    @DisplayName("Devrait exposer la comparaison entre imperatif et reactif")
    void shouldExposeProgrammingStyleComparison() {
        RestAssured.given()
                .when()
                .get("/taverne/reactif/comparaison")
                .then()
                .statusCode(200)
                .body("imperative", containsString("thread"))
                .body("reactive", containsString("servir ailleurs"))
                .body("uni", containsString("chope"))
                .body("multi", containsString("file de clients"));
    }
}
