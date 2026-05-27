package fr.eletutour.tavern.security.jwt.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
class TreasuryResourceTest {

    @Test
    @DisplayName("Sans token JWT, /ledger doit renvoyer 401")
    void testLedgerUnauthenticated() {
        given()
                .when().get("/api/tavern/ledger")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Sans token JWT, /vault doit renvoyer 401")
    void testVaultUnauthenticated() {
        given()
                .when().get("/api/tavern/vault")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Un accountant authentifie peut acceder a /ledger")
    @TestSecurity(user = "elora", roles = {"accountant"})
    void testLedgerAsAccountant() {
        given()
                .when().get("/api/tavern/ledger")
                .then()
                .statusCode(200)
                .body("book", equalTo("grand-ledger"))
                .body("entries", hasItem("Ale festival - 12 silver"));
    }

    @Test
    @DisplayName("Un accountant ne peut pas acceder a /vault (role insuffisant)")
    @TestSecurity(user = "elora", roles = {"accountant"})
    void testVaultAsAccountantForbidden() {
        given()
                .when().get("/api/tavern/vault")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Un treasurer authentifie peut acceder a /vault")
    @TestSecurity(user = "borin", roles = {"treasurer"})
    void testVaultAsTreasurer() {
        given()
                .when().get("/api/tavern/vault")
                .then()
                .statusCode(200)
                .body("vault", equalTo("iron-vault"))
                .body("status", equalTo("sealed"));
    }

    @Test
    @DisplayName("Un treasurer ne peut pas acceder a /ledger (role insuffisant)")
    @TestSecurity(user = "borin", roles = {"treasurer"})
    void testLedgerAsTreasurerForbidden() {
        given()
                .when().get("/api/tavern/ledger")
                .then()
                .statusCode(403);
    }
}
