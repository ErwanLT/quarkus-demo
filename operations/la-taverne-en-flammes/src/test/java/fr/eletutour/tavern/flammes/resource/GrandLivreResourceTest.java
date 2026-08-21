package fr.eletutour.tavern.flammes.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@DisplayName("Le grand livre reste coherent meme sous les flammes")
class GrandLivreResourceTest {

    @BeforeEach
    void purgerLeGrandLivre() {
        given().when().delete("/taverne/grand-livre").then().statusCode(200);
    }

    @Test
    @DisplayName("ecrit la tournee et la taxe de guilde dans la meme transaction")
    void doitEcrireLesDeuxLignesDeLaTournee() {
        given()
            .queryParam("aventurier", "Grimgor")
            .queryParam("montant", 30)
            .when().post("/taverne/grand-livre/ecritures")
            .then()
            .statusCode(201)
            .body("", hasSize(2))
            .body("[0].libelle", equalTo("Tournee de biere"))
            .body("[0].montantPiecesOr", equalTo(30))
            .body("[1].libelle", equalTo("Taxe de la guilde"))
            .body("[1].montantPiecesOr", equalTo(3));

        given()
            .when().get("/taverne/grand-livre")
            .then()
            .statusCode(200)
            .body("", hasSize(2));
    }

    @Test
    @DisplayName("annule tout quand l'incendie interrompt l'ecriture")
    void doitAnnulerLEcritureInterrompue() {
        given()
            .queryParam("aventurier", "Grimgor")
            .queryParam("montant", 30)
            .when().post("/taverne/grand-livre/ecritures-interrompues")
            .then()
            .statusCode(500)
            .body("title", equalTo("Erreur interne"))
            .body("status", equalTo(500));

        given()
            .when().get("/taverne/grand-livre")
            .then()
            .statusCode(200)
            .body("", hasSize(0));
    }

    @Test
    @DisplayName("commite l'ecriture lente quand elle reste sous le timeout de transaction")
    void doitCommiterLEcritureLenteSousLeTimeout() {
        given()
            .queryParam("aventurier", "Grimgor")
            .queryParam("montant", 12)
            .queryParam("attenteMs", 200)
            .when().post("/taverne/grand-livre/ecritures-lentes")
            .then()
            .statusCode(204);

        given()
            .when().get("/taverne/grand-livre")
            .then()
            .statusCode(200)
            .body("", hasSize(1))
            .body("[0].libelle", equalTo("Tournee de biere (base ralentie)"));
    }

    @Test
    @DisplayName("annule l'ecriture qui depasse le timeout de transaction")
    void doitAnnulerLEcritureQuiDepasseLeTimeoutDeTransaction() {
        given()
            .queryParam("aventurier", "Grimgor")
            .queryParam("montant", 12)
            .queryParam("attenteMs", 4_000)
            .when().post("/taverne/grand-livre/ecritures-lentes")
            .then()
            .statusCode(503)
            .body("title", equalTo("Transaction annulee"));

        given()
            .when().get("/taverne/grand-livre")
            .then()
            .statusCode(200)
            .body("", hasSize(0));
    }
}
