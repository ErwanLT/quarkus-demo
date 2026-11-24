package fr.eletutour.tavern.scheduler.resource;

import io.quarkus.scheduler.Scheduler;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class CorveesResourceTest {

    @Inject
    Scheduler scheduler;

    @Test
    @DisplayName("L'état des corvées est exposé sous forme de liste JSON")
    void exposeLesCorvees() {
        given()
                .when().get("/corvees")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", isA(List.class));
    }

    @Test
    @DisplayName("Une corvée peut être forcée sans attendre son échéance")
    void forceUneCorvee() {
        given()
                .when().post("/corvees/COMPTOIR")
                .then()
                .statusCode(200)
                .body("tache", equalTo("nettoyage-comptoir"))
                .body("executions", greaterThanOrEqualTo(1))
                .body("fil", notNullValue());

        given()
                .when().get("/corvees")
                .then()
                .statusCode(200)
                .body("tache", hasItem("nettoyage-comptoir"));
    }

    @Test
    @DisplayName("Une corvée inconnue renvoie 404")
    void refuseUneCorveeInconnue() {
        given()
                .when().post("/corvees/DRAGON")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Scheduler désactivé : getScheduledJobs() lève une exception")
    void schedulerDesactiveRefuseDeListerLesTaches() {
        assertFalse(scheduler.isStarted());
        assertThrows(UnsupportedOperationException.class, scheduler::getScheduledJobs);
    }

    @Test
    @DisplayName("La liste des tâches est vide quand le scheduler est désactivé")
    void listeLesTachesDuScheduler() {
        given()
                .when().get("/corvees/taches")
                .then()
                .statusCode(200)
                .body("$", empty());
    }
}
