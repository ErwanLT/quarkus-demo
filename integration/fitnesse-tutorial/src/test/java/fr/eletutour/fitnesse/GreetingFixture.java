package fr.eletutour.fitnesse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Fixture Slim volontairement fine : elle traduit une ligne de table en appel
 * HTTP vers l'endpoint Quarkus, et renvoie la réponse à FitNesse.
 */
public class GreetingFixture {

    private static final String BASE = "http://localhost:8126/taverne/accueil?nom=";

    private String nom;

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String accueil() throws Exception {
        String url = BASE + URLEncoder.encode(nom == null ? "" : nom, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IllegalStateException("HTTP " + response.statusCode() + " pour " + url);
        }
        return response.body();
    }
}
