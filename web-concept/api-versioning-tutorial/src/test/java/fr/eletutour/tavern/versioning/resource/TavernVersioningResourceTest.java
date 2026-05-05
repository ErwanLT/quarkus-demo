package fr.eletutour.tavern.versioning.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.eletutour.tavern.versioning.resource.TavernVersioningResource.API_VERSION_HEADER;
import static fr.eletutour.tavern.versioning.resource.TavernVersioningResource.MENU_V1_MEDIA_TYPE;
import static fr.eletutour.tavern.versioning.resource.TavernVersioningResource.MENU_V2_MEDIA_TYPE;
import static fr.eletutour.tavern.versioning.resource.UnknownMenuVersionExceptionMapper.PROBLEM_JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class TavernVersioningResourceTest {

    @Test
    @DisplayName("Devrait exposer une V1 et une V2 par version dans le path")
    void shouldVersionByPath() {
        RestAssured.given()
                .when().get("/taverne/versioning/path/v1/menu")
                .then()
                .statusCode(200)
                .body("plat", equalTo("Ragout de sanglier"))
                .body("prixPiecesCuivre", equalTo(12));

        RestAssured.given()
                .when().get("/taverne/versioning/path/v2/menu")
                .then()
                .statusCode(200)
                .body("plat", equalTo("Ragout de sanglier aux herbes de druide"))
                .body("prix.montant", equalTo(12))
                .body("prix.devise", equalTo("pieces-cuivre"));
    }

    @Test
    @DisplayName("Devrait choisir la version via un query parameter")
    void shouldVersionByParameter() {
        RestAssured.given()
                .queryParam("version", "2")
                .when().get("/taverne/versioning/parameter/menu")
                .then()
                .statusCode(200)
                .body("ingredients", hasItem("thym de druide"));
    }

    @Test
    @DisplayName("Devrait choisir la version via un header")
    void shouldVersionByHeader() {
        RestAssured.given()
                .header(API_VERSION_HEADER, "1")
                .when().get("/taverne/versioning/header/menu")
                .then()
                .statusCode(200)
                .body("plat", equalTo("Ragout de sanglier"))
                .body("prixPiecesCuivre", equalTo(12));
    }

    @Test
    @DisplayName("Devrait choisir la version par content negotiation avec media type")
    void shouldVersionByMediaTypeNegotiation() {
        RestAssured.given()
                .accept(MENU_V2_MEDIA_TYPE)
                .when().get("/taverne/versioning/negotiation/menu")
                .then()
                .statusCode(200)
                .contentType(startsWith(MENU_V2_MEDIA_TYPE))
                .body("disponiblePour", equalTo("aventuriers niveau 2 et plus"));

        RestAssured.given()
                .accept(MENU_V1_MEDIA_TYPE)
                .when().get("/taverne/versioning/negotiation/menu")
                .then()
                .statusCode(200)
                .contentType(startsWith(MENU_V1_MEDIA_TYPE))
                .body("plat", equalTo("Ragout de sanglier"));
    }

    @Test
    @DisplayName("Devrait refuser une version inconnue")
    void shouldRejectUnknownVersion() {
        RestAssured.given()
                .queryParam("version", "dragon")
                .when().get("/taverne/versioning/parameter/menu")
                .then()
                .statusCode(400)
                .contentType(startsWith(PROBLEM_JSON))
                .body("type", equalTo("https://eletutour.fr/problems/api-version-unknown"))
                .body("title", equalTo("Version d'API inconnue"))
                .body("status", equalTo(400))
                .body("code", equalTo("VERSION_INCONNUE"));
    }

    @Test
    @DisplayName("Devrait appliquer la meme erreur standardisee pour une version inconnue dans le header")
    void shouldRejectUnknownHeaderVersion() {
        RestAssured.given()
                .header(API_VERSION_HEADER, "lich")
                .when().get("/taverne/versioning/header/menu")
                .then()
                .statusCode(400)
                .contentType(startsWith(PROBLEM_JSON))
                .body("code", equalTo("VERSION_INCONNUE"));
    }
}
