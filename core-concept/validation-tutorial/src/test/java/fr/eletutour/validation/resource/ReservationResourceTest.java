package fr.eletutour.validation.resource;

import fr.eletutour.validation.business.TavernException;
import fr.eletutour.validation.model.ReservationRequest;
import fr.eletutour.validation.model.TypeChambre;
import fr.eletutour.validation.service.ReservationService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class ReservationResourceTest {

    @Inject
    ReservationService service;

    @Test
    void chambresDuRegistre() {
        given()
          .when().get("/validation/chambres")
          .then()
             .statusCode(200)
             .body("$", hasItems("DORTOIR", "SUITE", "PLACARD"));
    }

    @Test
    void reservationValide() {
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Bilbon", "bilbon@combe.fr", "SUITE", plusJours(11), plusJours(13), 2))
          .when().post("/validation/reservations")
          .then()
             .statusCode(201)
             .contentType(ContentType.JSON)
             .body("aventurier", is("Bilbon"))
             .body("chambre", is("SUITE"))
             .body("reference", startsWith("RES-"));

        given()
          .when().get("/validation/reservations")
          .then()
             .statusCode(200)
             .body("aventurier", hasItem("Bilbon"));
    }

    @Test
    void annotationsClassiquesDansLeFormatProblem() {
        given()
          .contentType(ContentType.JSON)
          .body(reservation("", "pas-un-courriel", "PLACARD", moinsJours(3), plusJours(1), 9))
          .when().post("/validation/reservations")
          .then()
             .statusCode(400)
             .contentType("application/problem+json")
             .body("title", is("Erreur de validation des données"))
             .body("detail", is("5 règle(s) de validation violée(s)."))
             .body("type", is("about:blank"))
             .body("instance", endsWith("/validation/reservations"))
             .body("timestamp", notNullValue())
             .body("additional.violations", is(5))
             .body("additional.errors.field", hasItems("aventurier", "courriel", "arrivee", "aventuriers"))
             .body("additional.errors.code", hasItems("NotBlank", "Size", "Email", "FutureOrPresent", "Max"));
    }

    @Test
    void courrielVideEstRejete() {
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Merlin", "", "SUITE", plusJours(16), plusJours(17), 1))
          .when().post("/validation/reservations")
          .then()
             .statusCode(400)
             .contentType("application/problem+json")
             .body("additional.errors.field", hasItem("courriel"))
             .body("additional.errors.code", hasItem("NotBlank"))
             .body("additional.errors.message", hasItem("Le courriel est requis"));
    }

    @Test
    void valeurInconnueRefuseeParLEnum() {
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Frodon", "frodon@combe.fr", "AUBERGE", plusJours(15), plusJours(17), 1))
          .when().post("/validation/reservations")
          .then()
             .statusCode(400)
             .contentType("application/problem+json")
             .body("title", is("Erreur de validation des données"))
             .body("detail", is("Type de chambre inconnu : AUBERGE (DORTOIR, SUITE ou PLACARD attendu)"))
             .body("additional.errors.field", hasItem("chambre"))
             .body("additional.errors.code", hasItem("TypeChambreInconnu"));
    }

    @Test
    void annotationCustomDeClasseSurPlusieursChamps() {
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Sam", "sam@combe.fr", "DORTOIR", plusJours(30), plusJours(38), 1))
          .when().post("/validation/reservations")
          .then()
             .statusCode(400)
             .contentType("application/problem+json")
             .body("additional.errors.field", hasItem("depart"))
             .body("additional.errors.code", hasItem("SejourCoherent"))
             .body("additional.errors.message", hasItem("Le séjour doit durer entre 1 et 7 nuits"));
    }

    @Test
    void conflitMetierApresLaValidation() {
        String body = reservation("Gandalf", "gandalf@combe.fr", "PLACARD", plusJours(21), plusJours(22), 1);

        given()
          .contentType(ContentType.JSON)
          .body(body)
          .when().post("/validation/reservations")
          .then()
             .statusCode(201);

        given()
          .contentType(ContentType.JSON)
          .body(body)
          .when().post("/validation/reservations")
          .then()
             .statusCode(409)
             .contentType("application/problem+json")
             .body("title", is("Chambre déjà réservée"))
             .body("type", is("urn:problem:tavern:CHAMBRE_DEJA_RESERVEE"))
             .body("detail", containsString("PLACARD"))
             .body("additional.code", is("CHAMBRE_DEJA_RESERVEE"));
    }

    @Test
    void chevauchementDeSejoursRefuseMaisSuiteContigueAcceptee() {
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Aragorn", "aragorn@combe.fr", "DORTOIR", plusJours(40), plusJours(42), 1))
          .when().post("/validation/reservations")
          .then()
             .statusCode(201);

        // Le second séjour commence avant la fin du premier : conflit.
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Legolas", "legolas@combe.fr", "DORTOIR", plusJours(41), plusJours(43), 1))
          .when().post("/validation/reservations")
          .then()
             .statusCode(409)
             .contentType("application/problem+json")
             .body("additional.code", is("CHAMBRE_DEJA_RESERVEE"));

        // Intervalles semi-ouverts : une arrivée le jour du départ est acceptée.
        given()
          .contentType(ContentType.JSON)
          .body(reservation("Gimli", "gimli@combe.fr", "DORTOIR", plusJours(42), plusJours(44), 1))
          .when().post("/validation/reservations")
          .then()
             .statusCode(201);
    }

    @Test
    void reservationsConcurrentesSurLaMemeChambre() throws Exception {
        int concurrence = 8;
        ExecutorService pool = Executors.newFixedThreadPool(concurrence);
        CountDownLatch depart = new CountDownLatch(1);
        List<Future<Boolean>> resultats = new ArrayList<>();

        try {
            for (int i = 0; i < concurrence; i++) {
                int numero = i;
                resultats.add(pool.submit(() -> {
                    depart.await();
                    try {
                        service.reserver(new ReservationRequest(
                                "Aventurier" + numero, "a" + numero + "@combe.fr", TypeChambre.SUITE,
                                plusJours(60), plusJours(61), 1));
                        return true;
                    } catch (TavernException conflit) {
                        return false;
                    }
                }));
            }
            depart.countDown();

            int reussites = 0;
            for (Future<Boolean> resultat : resultats) {
                if (resultat.get()) {
                    reussites++;
                }
            }
            assertThat(reussites, is(1));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void erreurJaxRsSansCauseDEnumGardeLeComportementGenerique() {
        given()
          .when().get("/validation/inexistant")
          .then()
             .statusCode(404)
             .contentType("application/problem+json")
             .body("title", is("Erreur lors du traitement de la requête"));
    }

    @Test
    void erreurInattenduePasseParLeMapperGlobal() {
        given()
          .when().get("/validation/erreur")
          .then()
             .statusCode(500)
             .contentType("application/problem+json")
             .body("title", is("Erreur interne du serveur"))
             .body("detail", is("Une erreur inattendue est survenue lors du traitement de votre requête."));
    }

    private String reservation(String aventurier, String courriel, String chambre,
                               LocalDate arrivee, LocalDate depart, int aventuriers) {
        return """
                {
                  "aventurier": "%s",
                  "courriel": "%s",
                  "chambre": "%s",
                  "arrivee": "%s",
                  "depart": "%s",
                  "aventuriers": %d
                }
                """.formatted(aventurier, courriel, chambre,
                arrivee.format(DateTimeFormatter.ISO_LOCAL_DATE),
                depart.format(DateTimeFormatter.ISO_LOCAL_DATE), aventuriers);
    }

    private LocalDate plusJours(int jours) {
        return LocalDate.now().plusDays(jours);
    }

    private LocalDate moinsJours(int jours) {
        return LocalDate.now().minusDays(jours);
    }
}
