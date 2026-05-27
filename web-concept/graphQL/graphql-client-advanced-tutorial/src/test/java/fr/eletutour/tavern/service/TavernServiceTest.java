package fr.eletutour.tavern.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestProfile(MockGraphQLResource.Profile.class)
class TavernServiceTest {

    @Test
    @DisplayName("GET /tavern/dynamic doit consommer le mock GraphQL et renvoyer la liste des aventuriers")
    void testGetAllAventurier() {
        given()
                .when().get("/tavern/dynamic")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].nom", equalTo("Baldric"))
                .body("[0].niveau", equalTo(7))
                .body("[1].nom", equalTo("Elara"))
                .body("[1].niveau", equalTo(5));
    }
}
