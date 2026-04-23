package fr.eletutour.tavern.batch.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import io.quarkus.test.junit.QuarkusTest;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TavernBatchResourceTest {

    @BeforeEach
    void resetSimulation() {
        given()
            .when().put("/tavern/brewery/stocks/reset")
            .then()
            .statusCode(200)
            .body("totalStock", equalTo(480))
            .body("totalConsumedToday", equalTo(0));
    }

    @Test
    void shouldBrewBackWhatWasConsumedDuringDay() {
        given()
            .contentType("application/json")
            .body(Map.of(
                "orders", Map.of(
                    "DRAGON_STOUT", 30,
                    "ELVEN_IPA", 20,
                    "DWARVEN_LAGER", 15
                )
            ))
            .when().post("/tavern/brewery/day-consumptions")
            .then()
            .statusCode(200)
            .body("totalRequested", equalTo(65))
            .body("totalServed", equalTo(65))
            .body("totalMissing", equalTo(0));

        given()
            .when().post("/tavern/brewery/nightly-batches")
            .then()
            .statusCode(201)
            .body("executionId", greaterThan(0))
            .body("jobName", equalTo("nightly-brew-job"))
            .body("batchStatus", equalTo("COMPLETED"))
            .body("totalBrewed", equalTo(65));

        given()
            .when().get("/tavern/brewery/stocks")
            .then()
            .statusCode(200)
            .body("totalStock", equalTo(480))
            .body("totalConsumedToday", equalTo(0));
    }

    @Test
    void shouldHandleMissingStockDuringDayAndOnlyBrewServedBeers() {
        given()
            .contentType("application/json")
            .body(Map.of(
                "orders", Map.of(
                    "DRAGON_STOUT", 130,
                    "GOBLIN_PALE_ALE", 10
                )
            ))
            .when().post("/tavern/brewery/day-consumptions")
            .then()
            .statusCode(200)
            .body("totalRequested", equalTo(140))
            .body("totalServed", equalTo(130))
            .body("totalMissing", equalTo(10));

        given()
            .when().post("/tavern/brewery/nightly-batches")
            .then()
            .statusCode(201)
            .body("batchStatus", equalTo("COMPLETED"))
            .body("totalBrewed", equalTo(130));

        given()
            .when().get("/tavern/brewery/stocks")
            .then()
            .statusCode(200)
            .body("totalStock", equalTo(480))
            .body("totalConsumedToday", equalTo(0));
    }

    @Test
    void shouldReturnProblemDetailsWhenRequestIsInvalid() {
        given()
            .contentType("application/json")
            .body(Map.of("orders", Map.of("DRAGON_STOUT", -1)))
            .when().post("/tavern/brewery/day-consumptions")
            .then()
            .statusCode(400)
            .body("title", equalTo("Requete invalide"))
            .body("status", equalTo(400))
            .body("detail", equalTo("Les quantites de biere doivent etre positives ou nulles"));
    }
}
