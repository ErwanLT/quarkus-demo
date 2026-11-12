package fr.eletutour.tavern.barde.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

/**
 * Tests du barde en mode JVM.
 *
 * <p>Ces mêmes tests sont rejoués contre le binaire natif par {@link BardeResourceIT}.
 * Ils couvrent volontairement les trois écueils du monde fermé : réflexion,
 * ressources et initialisation au build.</p>
 */
@QuarkusTest
class BardeResourceTest {

    @Test
    void leLuthierFabriqueUnInstrumentParSonNom() {
        given()
            .when().get("/barde/instruments/luth")
            .then()
            .statusCode(200)
            .body("instrument", is("luth"))
            .body("son", containsString("Plink"));
    }

    @Test
    void unInstrumentInconnuRenvoie404() {
        given()
            .when().get("/barde/instruments/cornemuse")
            .then()
            .statusCode(404)
            .body("erreur", containsString("cornemuse"));
    }

    @Test
    void leRepertoireEstLuDepuisLeParcheminJson() {
        given()
            .when().get("/barde/repertoire")
            .then()
            .statusCode(200)
            .body("$", hasItems("la-baleine-qui-tombe", "la-chope-de-l-aube"));
    }

    @Test
    void uneBalladeEstLueDepuisLesRessources() {
        given()
            .when().get("/barde/ballades/la-baleine-qui-tombe")
            .then()
            .statusCode(200)
            .body("titre", is("la-baleine-qui-tombe"))
            .body("couplets", hasSize(3));
    }

    @Test
    void laDateGraveeNEstPasAnterieureAuReveil() {
        JsonPath statue = given()
            .when().get("/barde/statue")
            .then()
            .statusCode(200)
            .body("mode", is(oneOf("jvm", "natif")))
            .extract().jsonPath();

        Instant dateGravee = Instant.parse(statue.getString("dateGravee"));
        Instant reveil = Instant.parse(statue.getString("reveil"));

        // Si la classe Sculpteur était initialisée au build, la date gravée
        // serait celle de la compilation, donc antérieure au réveil.
        assertFalse(dateGravee.isBefore(reveil),
                "Date gravée " + dateGravee + " antérieure au réveil " + reveil);
    }
}
