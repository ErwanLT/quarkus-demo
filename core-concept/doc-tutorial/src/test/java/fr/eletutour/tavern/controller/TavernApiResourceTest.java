package fr.eletutour.tavern.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class TavernApiResourceTest {

    @Test
    @DisplayName("GET /api/tavern/greeting?name=Arthas devrait saluer Arthas")
    void testGreetingWithName() {
        given()
                .queryParam("name", "Arthas")
                .when().get("/api/tavern/greeting")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("message", is("Welcome to the tavern, Arthas!"));
    }

    @Test
    @DisplayName("GET /api/tavern/greeting sans nom devrait utiliser 'adventurer'")
    void testGreetingWithoutName() {
        given()
                .when().get("/api/tavern/greeting")
                .then()
                .statusCode(200)
                .body("message", is("Welcome to the tavern, adventurer!"));
    }

    @Test
    @DisplayName("POST /api/tavern/order devrait renvoyer un reçu correspondant à la commande")
    void testOrder() {
        String body = "{\"item\": \"Mead\", \"quantity\": 5}";

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/tavern/order")
                .then()
                .statusCode(200)
                .body("item", is("Mead"))
                .body("quantity", is(5))
                .body("note", is("Order accepted by the tavern keeper."));
    }

    @Test
    @DisplayName("L'endpoint OpenAPI doit être exposé")
    void testOpenApiEndpoint() {
        given()
                .when().get("/openapi")
                .then()
                .statusCode(200);
    }
}
