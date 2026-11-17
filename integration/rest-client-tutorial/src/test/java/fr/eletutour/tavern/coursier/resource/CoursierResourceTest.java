package fr.eletutour.tavern.coursier.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class CoursierResourceTest {

    private static final String PROBLEM_JSON = "application/problem+json";

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
    @DisplayName("Le filtre de difficulté traverse le client REST, sans tenir compte de la casse")
    void filtreParDifficulte() {
        given()
                .queryParam("difficulte", "mortel")
                .when().get("/coursier/contrats")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].titre", equalTo("Le dragon de la combe"));
    }

    @Test
    @DisplayName("Une difficulté inconnue est une erreur de l'appelant : 400 problem+json")
    void difficulteInconnueRenvoieUn400() {
        given()
                .queryParam("difficulte", "EPIQUE")
                .when().get("/coursier/contrats")
                .then()
                .statusCode(400)
                .contentType(startsWith(PROBLEM_JSON))
                .body("type", equalTo("urn:problem:coursier:DIFFICULTE_INCONNUE"))
                .body("title", equalTo("Difficulté inconnue"))
                .body("detail", containsString("EPIQUE"))
                .body("additional.code", equalTo("DIFFICULTE_INCONNUE"));
    }

    @Test
    @DisplayName("Le coursier signe un contrat, transmet son nom et rapporte un bordereau")
    void accepteUnContrat() {
        given()
                .queryParam("coursier", "Brune la rôdeuse")
                .when().post("/coursier/contrats/1/acceptation")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("coursier", equalTo("Brune la rôdeuse"))
                .body("recompense", is(5))
                .body("bordereau", notNullValue());
    }

    @Test
    @DisplayName("Un contrat inconnu sort en problem+json 404 complet")
    void contratInconnuRenvoieUnProbleme() {
        given()
                .when().get("/coursier/contrats/99")
                .then()
                .statusCode(404)
                .contentType(startsWith(PROBLEM_JSON))
                .body("type", equalTo("urn:problem:coursier:CONTRAT_INTROUVABLE"))
                .body("title", equalTo("Contrat introuvable"))
                .body("status", is(404))
                .body("instance", endsWith("/coursier/contrats/99"))
                .body("timestamp", notNullValue())
                .body("additional.code", equalTo("CONTRAT_INTROUVABLE"));
    }

    @Test
    @DisplayName("Une panne de la Guilde sort en 502, sans relayer son message interne")
    void panneDistanteRenvoieUn502() {
        given()
                .when().get("/coursier/contrats/13")
                .then()
                .statusCode(502)
                .contentType(startsWith(PROBLEM_JSON))
                .body("type", equalTo("urn:problem:coursier:REPONSE_INATTENDUE"))
                .body("title", equalTo("Réponse inattendue d'un service distant"))
                .body("detail", not(containsString("registre")))
                .body("additional.code", equalTo("REPONSE_INATTENDUE"))
                .body("additional.service", equalTo("La Guilde"))
                .body("additional.statutDistant", is(500));
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
                .contentType(startsWith(PROBLEM_JSON))
                .body("type", equalTo("urn:problem:coursier:RECETTE_INTROUVABLE"))
                .body("title", equalTo("Recette introuvable"))
                .body("additional.code", equalTo("RECETTE_INTROUVABLE"));
    }

    @Test
    @DisplayName("Une route inconnue sort aussi en problem+json grâce au mapper global")
    void routeInconnueRenvoieUnProbleme() {
        given()
                .when().get("/coursier/cave-secrete")
                .then()
                .statusCode(404)
                .contentType(startsWith(PROBLEM_JSON))
                .body("title", equalTo("Erreur lors du traitement de la requête"))
                .body("instance", endsWith("/coursier/cave-secrete"));
    }

    @Test
    @DisplayName("La Guilde simulée refuse un appel sans le sceau de la taverne")
    void laGuildeRefuseUnAppelSansSceau() {
        given()
                .when().get("/guilde/contrats")
                .then()
                .statusCode(400)
                .contentType(startsWith(PROBLEM_JSON))
                .body("title", equalTo("Sceau manquant"));
    }

    @Test
    @DisplayName("La Guilde simulée accepte un appel qui porte le sceau")
    void laGuildeAccepteUnAppelScelle() {
        given()
                .header("X-Taverne", "The Falling Whale")
                .when().get("/guilde/contrats")
                .then()
                .statusCode(200)
                .body("$", hasSize(3));
    }
}
