package fr.eletutour.tavern.editique.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;

/**
 * Tests du comptoir de scan et de la fiche produit vers laquelle mènent les étiquettes.
 */
@QuarkusTest
class ScanResourceTest {

    @Test
    @DisplayName("Le comptoir affiche un champ prêt à recevoir la douchette")
    void pageDuComptoir() {
        RestAssured.given()
                .when().get("/taverne/scan")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Comptoir de scan"),
                        containsString("name=\"code\""),
                        containsString("autofocus"));
    }

    @Test
    @DisplayName("Un code saisi au comptoir redirige vers sa page (303 See Other)")
    void codeSaisi() {
        RestAssured.given()
                .redirects().follow(false)
                .queryParam("code", "CMD-0042")
                .when().get("/taverne/scan")
                .then()
                .statusCode(303)
                .header("Location", endsWith("/taverne/commandes/CMD-0042/suivi"));
    }

    @Test
    @DisplayName("Une appli mobile peut ouvrir directement l'URL modèle /taverne/scan/{code}")
    void urlModele() {
        RestAssured.given()
                .redirects().follow(false)
                .when().get("/taverne/scan/2000000000015")
                .then()
                .statusCode(303)
                .header("Location", endsWith("/taverne/produits/TON-ROUSSE"));
    }

    @Test
    @DisplayName("En suivant la redirection, l'étiquette ouvre la fiche du tonneau")
    void ficheProduit() {
        RestAssured.given()
                .when().get("/taverne/scan/2000000000015")
                .then()
                .statusCode(200)
                .body(containsString("Tonneau de rousse naine 50L"),
                        containsString("TON-ROUSSE"),
                        containsString("2000000000015"));
    }

    @Test
    @DisplayName("Un code mal lu reste au comptoir avec un 404 et la raison du refus")
    void codeMalLu() {
        RestAssured.given()
                .when().get("/taverne/scan/2000000000016")
                .then()
                .statusCode(404)
                .contentType(containsString("text/html"))
                .body(containsString("Clé de contrôle invalide"),
                        containsString("name=\"code\""));
    }

    @Test
    @DisplayName("Le code affiché dans le message d'erreur est échappé")
    void codeEchappe() {
        RestAssured.given()
                .queryParam("code", "<b>x</b>")
                .when().get("/taverne/scan")
                .then()
                .statusCode(404)
                .body(containsString("&lt;B&gt;X&lt;/B&gt;"));
    }

    @Test
    @DisplayName("Une référence de produit inconnue renvoie 404")
    void produitInconnu() {
        RestAssured.given()
                .when().get("/taverne/produits/FANTOME")
                .then()
                .statusCode(404);
    }
}
