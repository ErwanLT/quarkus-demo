package fr.eletutour.tavern.controller;

import fr.eletutour.tavern.client.TaverneClientApi;
import fr.eletutour.tavern.model.Aventurier;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class GuildeResourceTest {

    @InjectMock
    TaverneClientApi taverneClient;

    @Test
    @DisplayName("GET /guilde/aventuriers doit renvoyer la liste fournie par le client GraphQL")
    void testRecupererAventuriers() {
        Aventurier aventurier = new Aventurier();
        aventurier.setId(1L);
        aventurier.setNom("Baldric");
        aventurier.setClasse("Guerrier");
        aventurier.setNiveau(7);

        when(taverneClient.aventuriers()).thenReturn(List.of(aventurier));

        given()
                .when().get("/guilde/aventuriers")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].nom", equalTo("Baldric"))
                .body("[0].classe", equalTo("Guerrier"))
                .body("[0].niveau", equalTo(7));
    }

    @Test
    @DisplayName("GET /guilde/aventuriers doit renvoyer une liste vide si le client renvoie vide")
    void testRecupererAventuriersListeVide() {
        when(taverneClient.aventuriers()).thenReturn(List.of());

        given()
                .when().get("/guilde/aventuriers")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
}
