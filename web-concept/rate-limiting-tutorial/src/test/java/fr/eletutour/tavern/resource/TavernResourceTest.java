package fr.eletutour.tavern.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class TavernResourceTest {

    @Test
    void testRateLimiting() {
        // Les 3 premières commandes doivent passer (HTTP 200)
        for (int i = 0; i < 3; i++) {
            RestAssured.given()
                    .when().post("/taverne/commande")
                    .then()
                    .statusCode(200)
                    .body(equalTo("Tiens, une bonne pinte bien fraîche !"));
        }

        // La 4ème commande doit être rejetée (HTTP 429) car on dépasse la limite de 3/10s
        RestAssured.given()
                .when().post("/taverne/commande")
                .then()
                .statusCode(429)
                .body(equalTo("Holà l'ami ! Laisse-moi le temps de tirer ta bière, le tonneau n'est pas infini !"));
    }

    @Test
    void testRetry() {
        // On réinitialise notre compteur
        RestAssured.given().queryParam("reset", true).when().get("/taverne/cave").then().statusCode(200);

        // Cette requête va échouer 2 fois côté serveur en levant une exception.
        // Mais grâce à @Retry, Quarkus va automatiquement relancer la méthode, et la 3ème tentative passera.
        // Côté client (RestAssured), on ne s'en rend même pas compte, hormis le temps de réponse un peu plus long !
        RestAssured.given()
                .when().get("/taverne/cave")
                .then()
                .statusCode(200)
                .body(equalTo("Voilà votre prestigieux Vin Elfique millésimé !"));
    }

    @Test
    void testFallbackSuccess() {
        RestAssured.given()
                .queryParam("empty", false) // Il y a du plat du jour
                .when().get("/taverne/plat-du-jour")
                .then()
                .statusCode(200)
                .body(equalTo("Voici un délicieux ragoût de sanglier !"));
    }

    @Test
    void testFallbackFailure() {
        RestAssured.given()
                .queryParam("empty", true) // La marmite est vide, déclenchement du @Fallback
                .when().get("/taverne/plat-du-jour")
                .then()
                .statusCode(200) // Le Fallback réussit à nous donner autre chose ! HTTP 200 et non 500.
                .body(equalTo("Désolé l'ami, la marmite est vide... Tiens, un morceau de pain dur et du fromage sec en lot de consolation."));
    }
}
