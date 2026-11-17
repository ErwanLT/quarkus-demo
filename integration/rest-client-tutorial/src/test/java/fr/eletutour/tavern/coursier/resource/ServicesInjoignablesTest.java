package fr.eletutour.tavern.coursier.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * La Guilde et le Grimoire ont fermé boutique : les deux clients pointent
 * sur un port où personne n'écoute.
 */
@QuarkusTest
@TestProfile(ServicesInjoignablesTest.PortesCloses.class)
class ServicesInjoignablesTest {

    public static class PortesCloses implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.rest-client.guilde.url", "http://localhost:1",
                    "taverne.grimoire.url", "http://localhost:1");
        }
    }

    @Test
    @DisplayName("Guilde injoignable : 502 problem+json, sans fuite de l'adresse interne")
    void guildeInjoignable() {
        given()
                .when().get("/coursier/contrats")
                .then()
                .statusCode(502)
                .contentType(startsWith("application/problem+json"))
                .body("type", equalTo("urn:problem:coursier:SERVICE_INJOIGNABLE"))
                .body("title", equalTo("Service distant injoignable"))
                .body("detail", not(containsString("localhost")))
                .body("additional.code", equalTo("SERVICE_INJOIGNABLE"));
    }

    @Test
    @DisplayName("Grimoire injoignable : le client programmatique suit la même règle")
    void grimoireInjoignable() {
        given()
                .when().get("/coursier/grimoire/hydromel")
                .then()
                .statusCode(502)
                .body("type", equalTo("urn:problem:coursier:SERVICE_INJOIGNABLE"));
    }
}
