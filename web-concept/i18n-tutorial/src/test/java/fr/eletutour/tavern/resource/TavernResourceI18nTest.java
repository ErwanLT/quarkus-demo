package fr.eletutour.tavern.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class TavernResourceI18nTest {

    @Test
    void shouldReturnFrenchPluralForZeroInAffluenceEndpoint() {
        given()
                .header("Accept-Language", "fr-FR")
                .queryParam("nombre", 0)
                .when()
                .get("/taverne/affluence")
                .then()
                .statusCode(200)
                .body(containsString("0 aventuriers"));
    }

    @Test
    void shouldReturnEnglishPluralForZeroInAffluenceEndpoint() {
        given()
                .header("Accept-Language", "en-US")
                .queryParam("nombre", 0)
                .when()
                .get("/taverne/affluence")
                .then()
                .statusCode(200)
                .body(containsString("0 adventurers"));
    }

    @Test
    void shouldReturnLatinCurrencyForPriceEndpoint() {
        given()
                .header("Accept-Language", "la-LA")
                .queryParam("article", "potio")
                .queryParam("montant", 3.5)
                .when()
                .get("/taverne/tarifs")
                .then()
                .statusCode(200)
                .body(containsString("3,50 dn."));
    }

    @Test
    void shouldReturnGermanMessageForBeerOrder() {
        given()
                .header("Accept-Language", "de-DE")
                .when()
                .post("/taverne/commandes")
                .then()
                .statusCode(200)
                .body(containsString("ein schönes frisches Bier"));
    }
}
