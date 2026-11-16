package fr.eletutour.tavern.coursier.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class CoursierResourceTest {

    @Test
    @DisplayName("Le coursier relaie le tableau des contrats de la Guilde")
    void listeLesContratsViaLaGuilde() {
        given()
                .when().get("/coursier/contrats")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("[0].titre", equalTo("Rats dans la cave"));
    }

    @Test
    @DisplayName("Le filtre de difficulté traverse le client REST")
    void filtreParDifficulte() {
        given()
                .queryParam("difficulte", "MORTEL")
                .when().get("/coursier/contrats")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].titre", equalTo("Le dragon de la combe"));
    }

    @Test
    @DisplayName("Le coursier signe un contrat et transmet son nom")
    void accepteUnContrat() {
        given()
                .queryParam("coursier", "Brune la rôdeuse")
                .when().post("/coursier/contrats/1/acceptation")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("coursier", equalTo("Brune la rôdeuse"))
                .body("recompense", is(5));
    }

    @Test
    @DisplayName("Un contrat inconnu sort en problem+json 404")
    void contratInconnuRenvoieUnProbleme() {
        given()
                .when().get("/coursier/contrats/99")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"))
                .body("status", is(404))
                .body("title", equalTo("Contrat introuvable"));
    }

    @Test
    @DisplayName("Le client programmatique lit une recette du Grimoire")
    void litUneRecetteDuGrimoire() {
        given()
                .when().get("/coursier/grimoire/hydromel")
                .then()
                .statusCode(200)
                .body("nom", equalTo("Hydromel du barde"))
                .body("ingredients", hasSize(3));
    }

    @Test
    @DisplayName("Une recette absente sort en problem+json 404")
    void recetteInconnueRenvoieUnProbleme() {
        given()
                .when().get("/coursier/grimoire/potion-inconnue")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Recette introuvable"));
    }

    @Test
    @DisplayName("La Guilde refuse un appel sans le sceau de la taverne")
    void laGuildeRefuseUnAppelSansSceau() {
        given()
                .when().get("/guilde/contrats")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Sceau manquant"));
    }

    @Test
    @DisplayName("Le client déclaratif ajoute bien le sceau de la taverne")
    void leClientAjouteLeSceau() {
        given()
                .header("X-Taverne", "The Falling Whale")
                .when().get("/guilde/contrats")
                .then()
                .statusCode(200)
                .body("$", hasSize(3));
    }
}
