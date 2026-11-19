package fr.eletutour.tavern.archives.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
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
                .body("elements", hasSize(greaterThanOrEqualTo(4)))
                .body("elements.nom", hasItem("Brune la rôdeuse"))
                .body("total", greaterThanOrEqualTo(4));
    }

    @Test
    @DisplayName("Le filtre par classe ne renvoie que les bardes")
    void filtreParClasse() {
        given()
                .queryParam("classe", "BARDE")
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(200)
                .body("elements", hasSize(1))
                .body("elements[0].nom", equalTo("Aldric le barde"))
                .body("elements[0].classe", equalTo("BARDE"));
    }

    @Test
    @DisplayName("Le filtre par rang cherche dans le tableau imbriqué des prouesses")
    void filtreParRangImbrique() {
        given()
                .queryParam("rang", "DRAGON")
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(200)
                .body("elements", hasSize(1))
                .body("elements[0].nom", equalTo("Mirelle l'ensorceleuse"))
                .body("elements[0].prouesses.rang", hasItem("DRAGON"));
    }

    @Test
    @DisplayName("Un dossier se lit par son identifiant ObjectId")
    void litUnDossierParId() {
        String id = given()
                .queryParam("classe", "BARDE")
                .when().get("/archives/aventuriers")
                .then().statusCode(200)
                .extract().path("elements[0].id");

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
                .body("title", equalTo("Nom déjà pris"))
                .body("type", equalTo("urn:problem:archives:ARCHIVES_NOM_DEJA_PRIS"))
                .body("detail", equalTo("Un dossier porte déjà ce nom dans les archives."))
                .body("instance", endsWith("/archives/aventuriers"))
                .body("timestamp", notNullValue())
                .body("additional.code", equalTo("ARCHIVES_NOM_DEJA_PRIS"))
                .body("additional.nom", equalTo("Brune la rôdeuse"));
    }

    @Test
    @DisplayName("Un identifiant ObjectId inconnu sort en 404")
    void identifiantInconnu() {
        given()
                .when().get("/archives/aventuriers/000000000000000000000000")
                .then()
                .statusCode(404)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Dossier introuvable"))
                .body("type", equalTo("urn:problem:archives:ARCHIVES_DOSSIER_INTROUVABLE"))
                .body("detail", equalTo(
                        "Aucun dossier ne porte l'identifiant 000000000000000000000000."))
                .body("instance", endsWith("/archives/aventuriers/000000000000000000000000"))
                .body("timestamp", notNullValue())
                .body("additional.code", equalTo("ARCHIVES_DOSSIER_INTROUVABLE"))
                .body("additional.identifiant", equalTo("000000000000000000000000"));
    }

    @Test
    @DisplayName("Un identifiant mal formé sort en 400")
    void identifiantMalForme() {
        given()
                .when().get("/archives/aventuriers/pas-un-objectid")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Identifiant invalide"))
                .body("type", equalTo("urn:problem:archives:ARCHIVES_IDENTIFIANT_INVALIDE"))
                .body("instance", endsWith("/archives/aventuriers/pas-un-objectid"))
                .body("timestamp", notNullValue())
                .body("additional.code", equalTo("ARCHIVES_IDENTIFIANT_INVALIDE"))
                .body("additional.identifiant", equalTo("pas-un-objectid"));
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
                .body("elements", hasSize(2))
                .body("page", equalTo(0))
                .body("taille", equalTo(2))
                .body("total", greaterThanOrEqualTo(4));
    }

    @Test
    @DisplayName("Un DTO invalide produit un Problem de validation")
    void valideLeCorpsDUnDossier() {
        given()
                .contentType("application/json")
                .body("""
                        {"nom":"","classe":"MAGE","niveau":0,"bourse":-1}
                        """)
                .when().post("/archives/aventuriers")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Erreur de validation des données"))
                .body("additional.errors", notNullValue())
                .body("additional.errors.size()", greaterThanOrEqualTo(3))
                .body("instance", endsWith("/archives/aventuriers"))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Une contrainte sur un paramètre de requête suit le même format")
    void valideLaTailleDePage() {
        given()
                .queryParam("taille", 0)
                .when().get("/archives/aventuriers")
                .then()
                .statusCode(400)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Erreur de validation des données"))
                .body("additional.errors", notNullValue())
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Une prouesse invalide est refusée avant d'atteindre le service")
    void valideLeCorpsDUneProuesse() {
        given()
                .contentType("application/json")
                .body("""
                        {"titre":"","rang":null,"annee":0,"recompense":-1}
                        """)
                .when().post("/archives/aventuriers/000000000000000000000000/prouesses")
                .then()
                .statusCode(400)
                .body("title", equalTo("Erreur de validation des données"))
                .body("additional.errors", notNullValue());
    }

    @Test
    @DisplayName("Une erreur inattendue est masquée par le mapper global")
    void masqueLesErreursInattendues() {
        given()
                .when().get("/archives/erreur-test/boom")
                .then()
                .statusCode(500)
                .contentType(startsWith("application/problem+json"))
                .body("title", equalTo("Erreur interne du serveur"))
                .body("detail", equalTo(
                        "Une erreur inattendue est survenue lors du traitement de votre requête."))
                .body("instance", endsWith("/archives/erreur-test/boom"))
                .body("timestamp", notNullValue());
    }
}
