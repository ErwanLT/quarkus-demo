package fr.eletutour.fitnesse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Fixture de contrat API : elle envoie un POST JSON et expose le statut HTTP
 * et le corps de réponse à la table Slim.
 */
public class ReservationFixture {

    private int statut;
    private String corps;

    public void reserverEn(String nom, String chambre) throws Exception {
        String json = "{\"nom\":" + json(nom == null ? "" : nom)
                + ",\"chambre\":" + json(chambre == null ? "" : chambre) + "}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8126/taverne/reservation"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        statut = response.statusCode();
        corps = response.body();
    }

    public String statut() {
        return String.valueOf(statut);
    }

    public String confirmation() {
        return corps;
    }

    private String json(String valeur) {
        return "\"" + valeur.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
