package fr.eletutour.tavern.flammes.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
@DisplayName("Les sondes de sante refletent l'etat de l'incendie")
class SondesDeSanteTest {

    @BeforeEach
    void eteindreEtReconstruire() {
        given().when().delete("/taverne/incendie").then().statusCode(200);
    }

    @Test
    @DisplayName("la taverne est vivante et prete quand rien ne brule")
    void doitEtreVivanteEtPreteAuRepos() {
        given()
            .when().get("/q/health/live")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.name", hasItem("Charpente de la taverne"));

        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.name", hasItem("Acces a la reserve de biere"));
    }

    @Test
    @DisplayName("la readiness tombe quand la cuisine brule, la liveness reste UP")
    void doitPasserLaReadinessDownQuandLaCuisineBrule() {
        given()
            .queryParam("origine", "friture de gobelin")
            .when().post("/taverne/incendie")
            .then()
            .statusCode(200)
            .body("cuisineEnFeu", equalTo(true))
            .body("prete", equalTo(false))
            .body("vivante", equalTo(true));

        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(503)
            .body("status", equalTo("DOWN"))
            .body("checks.find { it.name == 'Acces a la reserve de biere' }.data.origine_incendie",
                equalTo("friture de gobelin"));

        given()
            .when().get("/q/health/live")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));

        given()
            .when().post("/taverne/incendie/extinctions")
            .then()
            .statusCode(200)
            .body("cuisineEnFeu", equalTo(false));

        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    @DisplayName("la liveness tombe quand la charpente cede")
    void doitPasserLaLivenessDownQuandLaCharpenteCede() {
        given()
            .when().post("/taverne/incendie/effondrements")
            .then()
            .statusCode(200)
            .body("charpenteRompue", equalTo(true))
            .body("vivante", equalTo(false));

        given()
            .when().get("/q/health/live")
            .then()
            .statusCode(503)
            .body("status", equalTo("DOWN"))
            .body("checks.find { it.name == 'Charpente de la taverne' }.data.poutres_maitresses",
                equalTo("rompues"));

        given()
            .when().post("/taverne/incendie/reconstructions")
            .then()
            .statusCode(200)
            .body("vivante", equalTo(true));
    }

    @Test
    @DisplayName("la chronologie garde la trace du tocsin")
    void doitHorodaterLesEtapesDeLIncident() {
        given()
            .queryParam("origine", "chaudron renverse")
            .when().post("/taverne/incendie")
            .then()
            .statusCode(200);

        given()
            .when().get("/taverne/incendie/chronologie")
            .then()
            .statusCode(200)
            .body("etape", contains("DEBUT_INCENDIE", "TOCSIN"))
            .body("[0].detail", equalTo("Le feu prend : chaudron renverse"))
            .body("[0].horodatage", org.hamcrest.Matchers.notNullValue());
    }
}
