package fr.eletutour.tavern.archives.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class ArchivesResourceTest {

    @Test
    @DisplayName("Les archives renvoient les dossiers semés au démarrage")
    void listeLesDossiers() {
        given()
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(4)))
                .body("nom", hasItem("Brune la rôdeuse"));
    }

    @Test
    @DisplayName("Le filtre par classe ne renvoie que les bardes")
    void filtreParClasse() {
        given()
                .queryParam("classe", "BARDE")
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].nom", equalTo("Aldric le barde"))
                .body("[0].classe", equalTo("BARDE"));
    }

    @Test
    @DisplayName("Le filtre par rang cherche dans le tableau imbriqué des prouesses")
    void filtreParRangImbrique() {
        given()
                .queryParam("rang", "DRAGON")
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].nom", equalTo("Mirelle l'ensorceleuse"))
                .body("[0].prouesses.rang", hasItem("DRAGON"));
    }

    @Test
    @DisplayName("Un dossier se lit par son identifiant ObjectId")
    void litUnDossierParId() {
        String id = given()
                .queryParam("classe", "BARDE")
                .when().get("/archives/aventuriers")
                .then().statusCode(200)
                .extract().path("[0].id");

        given()
                .when().get("/archives/aventuriers/" + id)
                .then()
                .statusCode(200)
                .body("nom", equalTo("Aldric le barde"))
                .body("id", equalTo(id));
    }

    @Test
    @DisplayName("Un nouveau dossier s'ajoute puis reçoit une prouesse")
    void creeUnDossierEtAjouteUneProuesse() {
        String nom = "Perceval " + UUID.randomUUID();

        String id = given()
                .contentType("application/json")
                .body("""
                        {"nom":"%s","classe":"PALADIN","niveau":1,"bourse":3}
                        """.formatted(nom))
                .when().post("/archives/aventuriers")
                .then()
                .statusCode(201)
                .body("nom", equalTo(nom))
                .body("classe", equalTo("PALADIN"))
                .extract().path("id");

        given()
                .contentType("application/json")
                .body("""
                        {"titre":"Le moulin hanté","rang":"GOBELIN","annee":1024,"recompense":7}
                        """)
                .when().post("/archives/aventuriers/" + id + "/prouesses")
                .then()
                .statusCode(200)
                .body("prouesses", hasSize(1))
                .body("prouesses[0].titre", equalTo("Le moulin hanté"))
                .body("prouesses[0].rang", equalTo("GOBELIN"));

        given()
                .when().get("/archives/aventuriers/" + id)
                .then()
                .statusCode(200)
                .body("prouesses[0].titre", equalTo("Le moulin hanté"));
    }

    @Test
    @DisplayName("Deux dossiers ne peuvent pas porter le même nom")
    void refuseUnNomDejaPris() {
        given()
                .contentType("application/json")
                .body("""
                        {"nom":"Brune la rôdeuse","classe":"RODEUR","niveau":1,"bourse":0}
                        """)
                .when().post("/archives/aventuriers")
                .then()
                .statusCode(409)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Nom déjà pris"));
    }

    @Test
    @DisplayName("Un identifiant ObjectId inconnu sort en 404")
    void identifiantInconnu() {
        given()
                .when().get("/archives/aventuriers/000000000000000000000000")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Dossier introuvable"));
    }

    @Test
    @DisplayName("Un identifiant mal formé sort en 400")
    void identifiantMalForme() {
        given()
                .when().get("/archives/aventuriers/pas-un-objectid")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Identifiant invalide"));
    }

    @Test
    @DisplayName("Les statistiques sont calculées par un pipeline d'agrégation")
    void calculeLesStatistiques() {
        given()
                .when().get("/archives/aventuriers/statistiques")
                .then()
                .statusCode(200)
                .body("find { it.classe == 'MAGE' }.effectif", greaterThanOrEqualTo(1))
                .body("find { it.classe == 'MAGE' }.bourse", greaterThanOrEqualTo(300));
    }

    @Test
    @DisplayName("La pagination limite le nombre de dossiers renvoyés")
    void pagineLesResultats() {
        given()
                .queryParam("page", 0)
                .queryParam("taille", 2)
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));
    }
}
