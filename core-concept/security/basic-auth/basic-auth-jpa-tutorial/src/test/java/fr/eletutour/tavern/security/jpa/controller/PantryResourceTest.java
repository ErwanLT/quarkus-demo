package fr.eletutour.tavern.security.jpa.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
class PantryResourceTest {

    @Test
    @DisplayName("Sans authentification, /pantry doit renvoyer 401")
    void testPantryUnauthenticated() {
        given()
                .when().get("/api/tavern/pantry")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Le keeper (stocke en JPA + bcrypt) peut acceder a /pantry")
    void testPantryAsKeeper() {
        given()
                .auth().preemptive().basic("keeper", "keeper123")
                .when().get("/api/tavern/pantry")
                .then()
                .statusCode(200)
                .body("area", equalTo("pantry"))
                .body("items", hasItem("bread"));
    }

    @Test
    @DisplayName("Le supplier est refuse sur /pantry (role insuffisant)")
    void testPantryAsSupplierForbidden() {
        given()
                .auth().preemptive().basic("supplier", "supplier123")
                .when().get("/api/tavern/pantry")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Mauvais mot de passe (bcrypt) -> 401")
    void testWrongPassword() {
        given()
                .auth().preemptive().basic("keeper", "wrong")
                .when().get("/api/tavern/pantry")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Utilisateur inexistant en base -> 401")
    void testUnknownUser() {
        given()
                .auth().preemptive().basic("ghost", "boo")
                .when().get("/api/tavern/pantry")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Le keeper et le supplier peuvent acceder a /cellar")
    void testCellarAccessibleByBothRoles() {
        given()
                .auth().preemptive().basic("keeper", "keeper123")
                .when().get("/api/tavern/cellar")
                .then()
                .statusCode(200)
                .body("area", equalTo("cellar"))
                .body("items", hasItem("ale"));

        given()
                .auth().preemptive().basic("supplier", "supplier123")
                .when().get("/api/tavern/cellar")
                .then()
                .statusCode(200)
                .body("area", equalTo("cellar"));
    }
}
