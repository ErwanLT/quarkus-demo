package fr.eletutour.tavern.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TaverneResourceTest {

    @Test
    @DisplayName("GraphQL: Devrait lister les aventuriers")
    void testAventuriersQuery() {
        String query = "{ \"query\": \"query { aventuriers { nom classe niveau } }\" }";

        given()
            .contentType(ContentType.JSON)
            .body(query)
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.aventuriers", hasSize(greaterThan(0)))
            .body("data.aventuriers[0].nom", is("Baldric"));
    }

    @Test
    @DisplayName("GraphQL: Devrait ajouter un aventurier via mutation")
    void testAjouterAventurierMutation() {
        String mutation = """
            {
              "query": "mutation($input: AventurierInput!) { ajouterAventurier(input: $input) { nom classe niveau } }",
              "variables": {
                "input": {
                  "nom": "Gimli",
                  "classe": "Guerrier Nain",
                  "niveau": 15
                }
              }
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(mutation)
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.ajouterAventurier.nom", is("Gimli"))
            .body("data.ajouterAventurier.classe", is("Guerrier Nain"));
    }

    @Test
    @DisplayName("GraphQL: Devrait retourner une erreur si l'input est incomplet (Validation GraphQL !)")
    void testAjouterAventurierInvalide() {
        // On omet le champ 'nom' qui est marqué @NonNull dans le schéma
        String mutationInvalide = """
            {
              "query": "mutation($input: AventurierInput!) { ajouterAventurier(input: $input) { nom } }",
              "variables": {
                "input": {
                  "classe": "Mage",
                  "niveau": 10
                }
              }
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(mutationInvalide)
        .when()
            .post("/graphql")
        .then()
            .statusCode(200) // GraphQL retourne 200 même avec des erreurs dans le body
            .body("errors", notNullValue());
    }
}
