package fr.eletutour.tavern.editique.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'intégration des routes REST de l'imprimerie de la taverne.
 */
@QuarkusTest
class ImprimerieResourceTest {

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static void assertCommencePar(String signature, byte[] contenu) {
        assertTrue(contenu.length > signature.length(), "le document reçu est vide");
        assertEquals(signature, new String(contenu, 0, signature.length(), StandardCharsets.ISO_8859_1));
    }

    @Test
    @DisplayName("Le ticket de caisse s'affiche directement dans le navigateur (inline)")
    void ticketDeCaisse() {
        byte[] pdf = RestAssured.given()
                .when().get("/taverne/commandes/CMD-0042/ticket")
                .then()
                .statusCode(200)
                .contentType(containsString("application/pdf"))
                .header("Content-Disposition", allOf(containsString("inline"), containsString("ticket-CMD-0042.pdf")))
                .extract().asByteArray();

        assertCommencePar("%PDF", pdf);
    }

    @Test
    @DisplayName("Le relevé des fournisseurs s'affiche directement dans le navigateur (inline)")
    void releveFournisseurs() {
        byte[] pdf = RestAssured.given()
                .when().get("/taverne/fournisseurs/releve")
                .then()
                .statusCode(200)
                .contentType(containsString("application/pdf"))
                .header("Content-Disposition", allOf(containsString("inline"), containsString("releve-fournisseurs.pdf")))
                .extract().asByteArray();

        assertCommencePar("%PDF", pdf);
    }

    @Test
    @DisplayName("La facture fournisseur se télécharge en PDF (attachment)")
    void facturePdf() {
        byte[] pdf = RestAssured.given()
                .when().get("/taverne/factures/FAC-BDF-2026-117")
                .then()
                .statusCode(200)
                .contentType(containsString("application/pdf"))
                .header("Content-Disposition",
                        allOf(containsString("attachment"), containsString("facture-FAC-BDF-2026-117.pdf")))
                .extract().asByteArray();

        assertCommencePar("%PDF", pdf);
    }

    @Test
    @DisplayName("La facture fournisseur s'exporte en classeur XLSX pour l'intendant")
    void factureXlsx() {
        byte[] xlsx = RestAssured.given()
                .queryParam("format", "xlsx")
                .when().get("/taverne/factures/FAC-BDF-2026-117")
                .then()
                .statusCode(200)
                .contentType(containsString(XLSX))
                .header("Content-Disposition", containsString("facture-FAC-BDF-2026-117.xlsx"))
                .extract().asByteArray();

        assertCommencePar("PK", xlsx);
    }

    @Test
    @DisplayName("La liste des factures contient celles de la brasserie et de la meunerie")
    void listeDesFactures() {
        RestAssured.given()
                .when().get("/taverne/factures")
                .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("numero", hasItems("FAC-BDF-2026-117", "FAC-MVD-2026-031"));
    }

    @Test
    @DisplayName("Une commande inconnue de l'aubergiste renvoie 404")
    void commandeInconnue() {
        RestAssured.given()
                .when().get("/taverne/commandes/CMD-9999/ticket")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Une facture inconnue des archives renvoie 404")
    void factureInconnue() {
        RestAssured.given()
                .when().get("/taverne/factures/FAC-FANTOME-0000")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Le nom de fichier ne garde que lettres, chiffres et tirets du numéro")
    void nomDeFichierAssaini() {
        assertEquals("facture-FAC-BDF-2026-117.pdf",
                ImprimerieResource.nomDeFichier("facture", "FAC-BDF-2026-117", "pdf"));
        assertEquals("ticket-CMD_0042___evil_.pdf",
                ImprimerieResource.nomDeFichier("ticket", "CMD 0042\"\r\nevil/", "pdf"));
    }
}
