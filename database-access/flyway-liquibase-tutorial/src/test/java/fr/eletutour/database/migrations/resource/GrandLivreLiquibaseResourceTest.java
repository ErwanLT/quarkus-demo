package fr.eletutour.database.migrations.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@DisplayName("Le grand livre Liquibase utilise le schema migre")
class GrandLivreLiquibaseResourceTest {

    @BeforeEach
    void purgerLeGrandLivre() {
        given().when().delete("/taverne/liquibase/grand-livre").then().statusCode(200);
    }

    @Test
    @DisplayName("ecrit la devise ajoutee par le changeSet 2.0 sur les deux lignes de la tournee")
    void doitEcrireLaDeviseAjouteeParLeChangeSet() {
        given()
            .queryParam("aventurier", "Grimgor")
            .queryParam("montant", 30)
            .when().post("/taverne/liquibase/grand-livre/ecritures")
            .then()
            .statusCode(201)
            .body("", hasSize(2))
            .body("[0].devise", equalTo("PIECE_OR"))
            .body("[1].devise", equalTo("PIECE_OR"));

        given()
            .when().get("/taverne/liquibase/grand-livre")
            .then()
            .statusCode(200)
            .body("", hasSize(2))
            .body("[0].libelle", equalTo("Tournee de biere"))
            .body("[1].libelle", equalTo("Taxe de la guilde"));
    }
}
