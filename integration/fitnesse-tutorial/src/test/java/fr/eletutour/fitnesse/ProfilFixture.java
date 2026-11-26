package fr.eletutour.fitnesse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Fixture utilisée par le scénario réutilisable de ScenarioTests : une seule
 * méthode, appelée avec les profils définis dans la page wiki.
 */
public class ProfilFixture {

    public String saluer(String nom) throws Exception {
        String url = "http://localhost:8126/taverne/accueil?nom="
                + URLEncoder.encode(nom == null ? "" : nom, StandardCharsets.UTF_8);
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IllegalStateException("HTTP " + response.statusCode() + " pour " + url);
        }
        return response.body();
    }
}
