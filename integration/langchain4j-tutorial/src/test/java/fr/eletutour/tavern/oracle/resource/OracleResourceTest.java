package fr.eletutour.tavern.oracle.resource;

import fr.eletutour.tavern.oracle.service.FakeTavernOracle;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class OracleResourceTest {
    @Inject
    FakeTavernOracle oracle;

    @BeforeEach
    void viderLeMiroir() {
        oracle.reinitialiser();
    }

    @Test
    @DisplayName("L'oracle reçoit la question telle quelle, sans appeler de modèle externe")
    void interrogeLOracleSansModeleExterne() {
        oracle.reponse = "L'hydromel du barde.";

        given()
                .queryParam("q", "Que proposes-tu ?")
        .when()
                .get("/oracle")
        .then()
                .statusCode(200)
                .body(is("L'hydromel du barde."));

        assertEquals(1, oracle.questions.size());
        assertEquals("Que proposes-tu ?", oracle.questions.get(0));
    }

    @Test
    @DisplayName("Le grimoire enrichit la question avec toutes les recettes contenant du miel")
    void enrichitLaQuestionAvecLesRecettesDuGrimoire() {
        oracle.reponse = "Le grimoire recommande l'hydromel.";

        given()
                .queryParam("q", "miel")
        .when()
                .get("/oracle/grimoire")
        .then()
                .statusCode(200)
                .body(is("Le grimoire recommande l'hydromel."));

        String prompt = oracle.prompts.get(0);
        assertTrue(prompt.contains("Question de l'aventurier : miel"));
        assertTrue(prompt.contains("Hydromel du Barde"));
        assertTrue(prompt.contains("Potion de vigueur"));
        assertTrue(prompt.contains("Thé Noir du Rôdeur"));
        assertTrue(prompt.contains("Lait d’Amande des Elfes"));
    }

    @Test
    @DisplayName("Un ingrédient partagé remonte plusieurs recettes")
    void trouvePlusieursRecettesPourUnIngredient() {
        given()
                .queryParam("q", "gingembre")
        .when()
                .get("/oracle/grimoire")
        .then()
                .statusCode(200);

        String prompt = oracle.prompts.get(0);
        assertTrue(prompt.contains("Potion de vigueur"));
        assertTrue(prompt.contains("Tonic de Mandragore"));
    }

    @Test
    @DisplayName("Une recherche accentuée (mûres) retrouve la recette correspondante")
    void trouveUneRecetteAvecDesAccents() {
        given()
                .queryParam("q", "mûres")
        .when()
                .get("/oracle/grimoire")
        .then()
                .statusCode(200);

        assertTrue(oracle.prompts.get(0).contains("Jus de Mûres Sauvages"));
    }

    @Test
    @DisplayName("Sans recette correspondante, le prompt le signale au modèle")
    void signaleUnGrimoireSansRecetteCorrespondante() {
        given()
                .queryParam("q", "licorne-de-cristal")
        .when()
                .get("/oracle/grimoire")
        .then()
                .statusCode(200);

        assertTrue(oracle.prompts.get(0).contains("Aucun passage pertinent trouvé dans le grimoire."));
    }

    @Test
    @DisplayName("Une question vide n'appelle pas le modèle")
    void refuseUneQuestionVideSansAppelerLeModele() {
        given()
                .queryParam("q", " ")
        .when()
                .get("/oracle")
        .then()
                .statusCode(200)
                .body(is("Posez une question avec ?q=..."));

        assertTrue(oracle.questions.isEmpty());
        assertTrue(oracle.prompts.isEmpty());
    }
}
