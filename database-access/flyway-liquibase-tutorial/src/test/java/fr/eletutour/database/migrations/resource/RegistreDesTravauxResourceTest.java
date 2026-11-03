package fr.eletutour.database.migrations.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@DisplayName("Le registre des travaux compare Flyway et Liquibase")
class RegistreDesTravauxResourceTest {

    @Test
    @DisplayName("applique les deux migrations Flyway")
    void doitAppliquerLesMigrationsFlyway() {
        given()
            .when().get("/taverne/registre/flyway")
            .then()
            .statusCode(200)
            .body("outil", equalTo("flyway"))
            .body("versionCourante", equalTo("2"))
            .body("travauxAppliques", hasSize(2))
            .body("travauxAppliques[0].version", equalTo("1"))
            .body("travauxAppliques[0].etat", equalTo("SUCCESS"))
            .body("travauxAppliques[1].version", equalTo("2"))
            .body("travauxAppliques[1].etat", equalTo("SUCCESS"))
            .body("travauxEnAttente", hasSize(0));
    }

    @Test
    @DisplayName("applique les deux changeSets Liquibase")
    void doitAppliquerLesChangeSetsLiquibase() {
        given()
            .when().get("/taverne/registre/liquibase")
            .then()
            .statusCode(200)
            .body("outil", equalTo("liquibase"))
            .body("versionCourante", equalTo("2-ajout-de-la-devise"))
            .body("travauxAppliques", hasSize(2))
            .body("travauxAppliques[0].version", equalTo("1-creation-du-grand-livre"))
            .body("travauxAppliques[0].auteur", equalTo("taverne"))
            .body("travauxAppliques[0].etat", equalTo("SUCCESS"))
            .body("travauxAppliques[1].version", equalTo("2-ajout-de-la-devise"))
            .body("travauxAppliques[1].etat", equalTo("SUCCESS"))
            .body("travauxEnAttente", hasSize(0));
    }
}
