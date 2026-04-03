package fr.eletutour.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class TavernResourceTest {

    @Test
    void testGetTavernNotFound() {
        given()
          .when().get("/taverns/Inexistante")
          .then()
             .statusCode(404)
             .contentType("application/problem+json")
             .body("title", is("Ressource introuvable"))
             .body("status", is(404))
             .body("detail", is("La taverne 'Inexistante' n'existe pas."));
    }

    @Test
    void testCreateTavernValidationError() {
        String body = "{\"name\": \"\", \"city\": \"\", \"maxCapacity\": 1000}";
        given()
          .contentType(ContentType.JSON)
          .body(body)
          .when().post("/taverns")
          .then()
             .statusCode(400)
             .contentType("application/problem+json")
             .body("title", is("Erreur de validation des données"))
             .body("additional.errors", notNullValue());
    }

    @Test
    void testTavernAlreadyExists() {
        String body = "{\"name\": \"LePoneyFringant\", \"city\": \"Bree\", \"maxCapacity\": 50}";
        given()
          .contentType(ContentType.JSON)
          .body(body)
          .when().post("/taverns")
          .then()
             .statusCode(409)
             .body("title", is("Conflit de données"));
    }

    @Test
    void testCapacityReached() {
        // LePoneyFringant a une capacité de 50
        given()
          .when().post("/taverns/LePoneyFringant/enter?count=60")
          .then()
             .statusCode(422)
             .body("title", is("Capacité atteinte"))
             .body("additional.maxCapacity", is(50))
             .body("additional.availableSlots", is(50));
    }

    @Test
    void testGlobalError() {
        given()
          .when().get("/taverns/error")
          .then()
             .statusCode(500)
             .body("title", is("Erreur interne du serveur"));
    }
}
