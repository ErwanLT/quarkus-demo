package fr.eletutour.tavern.web.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class TavernWebResourceTest {

    @Test
    @DisplayName("La page d'accueil doit etre servie en HTML et contenir le titre 'Bienvenue'")
    void testIndex() {
        given()
                .when().get("/tavern-web")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Bienvenue"));
    }

    @Test
    @DisplayName("La carte des boissons doit lister un breuvage connu")
    void testDrinks() {
        given()
                .when().get("/tavern-web/drinks")
                .then()
                .statusCode(200)
                .body(containsString("Pan Galactic Gargle Blaster"));
    }

    @Test
    @DisplayName("La carte des mets doit lister un plat connu")
    void testFood() {
        given()
                .when().get("/tavern-web/food")
                .then()
                .statusCode(200)
                .body(containsString("Pain de Voyageur"));
    }

    @Test
    @DisplayName("La page de reservation doit afficher le formulaire et la suite par defaut")
    void testBookingForm() {
        given()
                .when().get("/tavern-web/booking")
                .then()
                .statusCode(200)
                .body(containsString("Suite Galactique"));
    }

    @Test
    @DisplayName("Un POST /booking valide doit afficher la page de confirmation")
    void testBookingSuccess() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("adventurerName", "Arthur Dent")
                .formParam("arrivalDate", "2026-06-01")
                .formParam("nights", 3)
                .formParam("roomType", "Suite Galactique")
                .when().post("/tavern-web/booking")
                .then()
                .statusCode(200)
                .body(containsString("Arthur Dent"))
                .body(containsString("Suite Galactique"));
    }

    @Test
    @DisplayName("Un POST /booking avec nom vide doit reafficher le formulaire avec l'erreur")
    void testBookingValidationError() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("adventurerName", "")
                .formParam("arrivalDate", "2026-06-01")
                .formParam("nights", 3)
                .formParam("roomType", "Suite Galactique")
                .when().post("/tavern-web/booking")
                .then()
                .statusCode(200)
                .body(containsString("Le nom est obligatoire"));
    }

    @Test
    @DisplayName("Un POST /booking avec date invalide doit reafficher le formulaire avec l'erreur de date")
    void testBookingInvalidDate() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("adventurerName", "Ford Prefect")
                .formParam("arrivalDate", "pas-une-date")
                .formParam("nights", 2)
                .formParam("roomType", "Suite Galactique")
                .when().post("/tavern-web/booking")
                .then()
                .statusCode(200)
                .body(containsString("date est invalide"));
    }

    @Test
    @DisplayName("Un POST /booking avec nights > 10 doit declencher la validation")
    void testBookingTooManyNights() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("adventurerName", "Zaphod Beeblebrox")
                .formParam("arrivalDate", "2026-06-01")
                .formParam("nights", 42)
                .formParam("roomType", "Suite Galactique")
                .when().post("/tavern-web/booking")
                .then()
                .statusCode(200)
                .body(containsString("ne doit pas dépasser 10"));
    }

    @Test
    @DisplayName("La page admin doit etre servie (registre des reservations)")
    void testAdminPage() {
        given()
                .when().get("/tavern-web/admin")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }
}
