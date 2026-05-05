package fr.eletutour.observability.advanced;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AdvancedObservabilityResourceTest {

    @Test
    void shouldExposeHealthReadiness() {
        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.name", hasItem("tavern-advanced-readiness"));
    }

    @Test
    void shouldExposeMetricsAfterOrder() {
        given()
            .queryParam("drink", "stout")
            .when().get("/advanced-tavern/order")
            .then()
            .statusCode(200)
            .body("drink", equalTo("stout"))
            .body("status", equalTo("SERVED"))
            .body("servedAt", notNullValue());

        given()
            .when().get("/q/metrics")
            .then()
            .statusCode(200)
            .body(containsString("tavern_advanced_order_count_total"))
            .body(containsString("tavern_advanced_order_seconds_count"));
    }

    @Test
    void shouldDefaultDrinkToAleWhenMissing() {
        given()
            .when().get("/advanced-tavern/order")
            .then()
            .statusCode(200)
            .body("drink", equalTo("ale"))
            .body("status", equalTo("SERVED"))
            .body("servedAt", notNullValue());
    }

    @Test
    void shouldDefaultDrinkToAleWhenBlank() {
        given()
            .queryParam("drink", "   ")
            .when().get("/advanced-tavern/order")
            .then()
            .statusCode(200)
            .body("drink", equalTo("ale"))
            .body("status", equalTo("SERVED"))
            .body("servedAt", notNullValue());
    }
}
