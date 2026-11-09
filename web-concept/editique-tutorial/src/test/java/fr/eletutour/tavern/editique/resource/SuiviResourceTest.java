package fr.eletutour.tavern.editique.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests des pages HTML ciblées par les QR codes imprimés.
 */
@QuarkusTest
class SuiviResourceTest {

    @Test
    @DisplayName("La page de suivi d'une commande reprend la table, le serveur et le total")
    void suiviCommande() {
        RestAssured.given()
                .when().get("/taverne/commandes/CMD-0042/suivi")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Commande CMD-0042"),
                        containsString("Table du Dragon"),
                        containsString("Brunehilde"),
                        containsString("Télécharger le ticket de caisse"));
    }

    @Test
    @DisplayName("La page de suivi d'une facture renvoie vers le PDF de la facture")
    void suiviFacture() {
        RestAssured.given()
                .when().get("/taverne/factures/FAC-BDF-2026-117/suivi")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Facture FAC-BDF-2026-117"),
                        containsString("Brasserie Barbe-de-Fer"),
                        containsString("Télécharger la facture"));
    }

    @Test
    @DisplayName("Les montants de la facture suivent le format des PDF : séparateur de milliers français")
    void montantsFormatesCommeLePdf() {
        String page = RestAssured.given()
                .when().get("/taverne/factures/FAC-BDF-2026-117/suivi")
                .then().statusCode(200)
                .extract().asString();
        // Selon le formateur, le séparateur de milliers est U+202F ou U+00A0 : on normalise en espace simple.
        String normalisee = page.replace('\u202f', ' ').replace('\u00a0', ' ');
        assertTrue(normalisee.contains("1 378,69 po"), "Montant attendu absent de la page : " + normalisee);
    }

    @Test
    @DisplayName("Le menu rappelle la table scannée et liste les produits de la taverne")
    void menuDeLaTable() {
        RestAssured.given()
                .queryParam("table", "Table du Dragon")
                .when().get("/taverne/menu")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Carte de la Table du Dragon"),
                        containsString("Hydromel des Elfes"),
                        containsString("HYD-ELF"));
    }

    @Test
    @DisplayName("Le nom de table est échappé avant d'être injecté dans le HTML")
    void tableEchappee() {
        RestAssured.given()
                .queryParam("table", "<script>alert(1)</script>")
                .when().get("/taverne/menu")
                .then()
                .statusCode(200)
                .body(containsString("&lt;script&gt;alert(1)&lt;/script&gt;"),
                        not(containsString("<script>alert(1)</script>")));
    }

    @Test
    @DisplayName("Une commande ou une facture inconnue renvoie 404")
    void suivisInconnus() {
        RestAssured.given()
                .when().get("/taverne/commandes/CMD-9999/suivi")
                .then()
                .statusCode(404);
        RestAssured.given()
                .when().get("/taverne/factures/FAC-FANTOME-0000/suivi")
                .then()
                .statusCode(404);
    }
}
