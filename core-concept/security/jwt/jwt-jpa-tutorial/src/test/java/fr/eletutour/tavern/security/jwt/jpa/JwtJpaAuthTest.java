package fr.eletutour.tavern.security.jwt.jpa;

import fr.eletutour.tavern.security.jwt.jpa.model.TavernUser;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class JwtJpaAuthTest {

    @BeforeEach
    @TestTransaction
    void seedUsers() {
        TavernUser.deleteAll();

        TavernUser accountant = new TavernUser();
        accountant.username = "elora";
        accountant.password = BcryptUtil.bcryptHash("ledger123");
        accountant.role = "accountant";
        accountant.persist();

        TavernUser treasurer = new TavernUser();
        treasurer.username = "borin";
        treasurer.password = BcryptUtil.bcryptHash("vault123");
        treasurer.role = "treasurer";
        treasurer.persist();
    }

    @Test
    void login_should_return_token_for_accountant() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"elora\",\"password\":\"ledger123\"}")
        .when()
                .post("/api/tavern/auth/login")
        .then()
                .statusCode(200)
                .body("token", not(isEmptyOrNullString()))
                .body("tokenType", equalTo("Bearer"))
                .body("expiresAt", greaterThan(0));
    }

    @Test
    void login_should_fail_with_invalid_password() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"elora\",\"password\":\"wrong\"}")
        .when()
                .post("/api/tavern/auth/login")
        .then()
                .statusCode(401);
    }

    @Test
    void accountant_token_can_access_ledger() {
        String token = loginToken("elora", "ledger123");

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/api/tavern/ledger")
        .then()
                .statusCode(200)
                .body("book", equalTo("grand-ledger"))
                .body("entries", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    void accountant_token_cannot_access_vault() {
        String token = loginToken("elora", "ledger123");

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/api/tavern/vault")
        .then()
                .statusCode(403);
    }

    @Test
    void treasurer_token_can_access_vault() {
        String token = loginToken("borin", "vault123");

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/api/tavern/vault")
        .then()
                .statusCode(200)
                .body("vault", equalTo("iron-vault"))
                .body("assets", hasSize(greaterThanOrEqualTo(1)));
    }

    private String loginToken(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password))
        .when()
                .post("/api/tavern/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
