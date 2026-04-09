package fr.eletutour.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

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
             .body("detail", is("La taverne 'Inexistante' n'existe pas."))
             .body("type", is("urn:problem:tavern:TAVERN_NOT_FOUND"))
             .body("instance", endsWith("/taverns/Inexistante"))
             .body("timestamp", notNullValue())
             .body("additional.code", is("TAVERN_NOT_FOUND"));
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
             .body("additional.errors", notNullValue())
             .body("instance", endsWith("/taverns"))
             .body("timestamp", notNullValue());
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
             .body("title", is("Conflit de données"))
             .body("type", is("urn:problem:tavern:TAVERN_ALREADY_EXISTS"))
             .body("instance", endsWith("/taverns"))
             .body("timestamp", notNullValue())
             .body("additional.code", is("TAVERN_ALREADY_EXISTS"));
    }

    @Test
    void testCapacityReached() {
        // LePoneyFringant a une capacité de 50
        given()
          .when().post("/taverns/LePoneyFringant/enter?count=60")
             .then()
             .statusCode(422)
             .body("title", is("Capacité atteinte"))
             .body("type", is("urn:problem:tavern:TAVERN_CAPACITY_REACHED"))
             .body("additional.maxCapacity", is(50))
             .body("additional.availableSlots", is(50))
             .body("instance", containsString("/taverns/LePoneyFringant/enter?count=60"))
             .body("timestamp", notNullValue())
             .body("additional.code", is("TAVERN_CAPACITY_REACHED"));
    }

    @Test
    void testGlobalError() {
        given()
             .when().get("/taverns/error")
             .then()
             .statusCode(500)
             .body("title", is("Erreur interne du serveur"))
             .body("instance", endsWith("/taverns/error"))
             .body("timestamp", notNullValue());
    }
}
