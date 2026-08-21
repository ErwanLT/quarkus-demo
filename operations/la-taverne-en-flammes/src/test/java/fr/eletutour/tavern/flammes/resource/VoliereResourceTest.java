package fr.eletutour.tavern.flammes.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@DisplayName("La voliere vidange les corbeaux deja recuperes")
class VoliereResourceTest {

    private static final int TENTATIVES_MAX = 100;
    private static final long ATTENTE_ENTRE_TENTATIVES_MS = 50L;

    @Test
    @DisplayName("finit de traiter tous les plis laches")
    void doitFinirDeTraiterTousLesPlis() {
        given().when().delete("/taverne/voliere").then().statusCode(200);

        given()
            .queryParam("nombre", 10)
            .when().post("/taverne/voliere/lachers")
            .then()
            .statusCode(200)
            .body("corbeauxLaches", equalTo(10));

        long traites = attendreLaVidange(10L);
        assertEquals(10L, traites, "tous les plis doivent avoir ete lus avant l'arret");

        given()
            .when().get("/taverne/voliere")
            .then()
            .statusCode(200)
            .body("corbeauxEnVol", equalTo(0));
    }

    @Test
    @DisplayName("ramene un lacher demesure a la limite autorisee")
    void doitLimiterLeNombreDeCorbeauxParLacher() {
        given().when().delete("/taverne/voliere").then().statusCode(200);

        given()
            .queryParam("nombre", 5_000)
            .when().post("/taverne/voliere/lachers")
            .then()
            .statusCode(200)
            .body("corbeauxLaches", equalTo(500));

        attendreLaVidange(500L);
        given().when().delete("/taverne/voliere").then().statusCode(200);
    }

    private long attendreLaVidange(long attendus) {
        long traites = 0L;
        for (int tentative = 0; tentative < TENTATIVES_MAX && traites < attendus; tentative++) {
            traites = given()
                .when().get("/taverne/voliere")
                .then()
                .statusCode(200)
                .extract().jsonPath().getLong("corbeauxTraites");
            if (traites < attendus) {
                patienter();
            }
        }
        return traites;
    }

    private void patienter() {
        try {
            Thread.sleep(ATTENTE_ENTRE_TENTATIVES_MS);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Attente de la vidange interrompue", interruptedException);
        }
    }
}
