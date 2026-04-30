package fr.eletutour.database.panache.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
public class GrimoireResourceTest {

    @Test
    public void testIngredientsEndpoint() {
        given()
          .when().get("/grimoire/ingredients")
          .then()
             .statusCode(200)
             .body("size()", is(4))
             .body("name", hasItems("Queue de Phénix", "Malt de Nain", "Eau de source elfique", "Basilic séché"));
    }

    @Test
    public void testIngredientsPagedEndpoint() {
        given()
          .queryParam("page", 0)
          .queryParam("size", 2)
          .when().get("/grimoire/ingredients/paged")
          .then()
             .statusCode(200)
             .body("size()", is(2));
    }

    @Test
    public void testRecipesEndpoint() {
        given()
          .when().get("/grimoire/recipes")
          .then()
             .statusCode(200)
             .body("size()", is(2))
             .body("title", hasItems("Hydromel de l'Elfe", "Ragoût de Basilic"));
    }

    @Test
    public void testSearchRecipesEndpoint() {
        given()
          .queryParam("title", "Hydromel")
          .when().get("/grimoire/recipes/search")
          .then()
             .statusCode(200)
             .body("size()", is(1))
             .body("[0].title", is("Hydromel de l'Elfe"));
    }
}
