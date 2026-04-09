package fr.eletutour.observability;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ObservabilityResourceTest {

    @Test
    void shouldExposeHealthReadiness() {
        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks[0].name", containsString("tavern-readiness"));
    }

    @Test
    void shouldExposeMetricsAfterOrder() {
        given()
            .queryParam("drink", "stout")
            .when().get("/tavern/order")
            .then()
            .statusCode(200)
            .body("drink", equalTo("stout"))
            .body("status", equalTo("SERVED"))
            .body("servedAt", notNullValue());

        given()
            .when().get("/q/metrics")
            .then()
            .statusCode(200)
            .body(containsString("tavern_order_count_total"))
            .body(containsString("tavern_order_seconds_count"));
    }
}
