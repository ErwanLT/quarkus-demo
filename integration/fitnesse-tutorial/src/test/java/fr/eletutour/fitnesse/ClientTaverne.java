package fr.eletutour.fitnesse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Petit client HTTP partagé par les fixtures : elles restent ainsi centrées
 * sur le vocabulaire métier des pages wiki, pas sur la plomberie HTTP.
 */
final class ClientTaverne {

    static final String BASE = System.getProperty("taverne.url", "http://localhost:8126");

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper JSON = new ObjectMapper();

    private ClientTaverne() {
    }

    static HttpResponse<String> get(String chemin) {
        return envoyer(requete(chemin).GET().build());
    }

    static HttpResponse<String> post(String chemin, String json) {
        return envoyer(requete(chemin)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build());
    }

    static HttpResponse<String> delete(String chemin) {
        return envoyer(requete(chemin).DELETE().build());
    }

    static JsonNode json(String corps) {
        try {
            return JSON.readTree(corps);
        } catch (IOException e) {
            throw new IllegalStateException("Réponse non JSON : " + corps, e);
        }
    }

    static ObjectNode objet() {
        return JSON.createObjectNode();
    }

    static String encoder(String valeur) {
        return URLEncoder.encode(valeur == null ? "" : valeur, StandardCharsets.UTF_8);
    }

    /** Les tables wiki parlent français : « oui » / « non » plutôt que true / false. */
    static boolean ouiNon(String valeur) {
        return switch (valeur == null ? "" : valeur.trim().toLowerCase()) {
            case "oui", "true", "vrai" -> true;
            case "non", "false", "faux", "" -> false;
            default -> throw new IllegalArgumentException("Répondre par oui ou non, pas : " + valeur);
        };
    }

    private static HttpRequest.Builder requete(String chemin) {
        return HttpRequest.newBuilder(URI.create(BASE + chemin)).timeout(Duration.ofSeconds(10));
    }

    private static HttpResponse<String> envoyer(HttpRequest requete) {
        try {
            return HTTP.send(requete, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("La taverne ne répond pas sur " + requete.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
