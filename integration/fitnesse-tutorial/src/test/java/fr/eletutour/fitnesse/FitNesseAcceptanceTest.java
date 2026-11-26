package fr.eletutour.fitnesse;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class FitNesseAcceptanceTest {

    private static final String FITNESSE = "http://localhost:8127";

    @Test
    void lesPagesFitNesseExercentLEndpointEtLIhm() throws Exception {
        // Les pages .wiki sont la source de vérité : FitNesse lit directement
        // src/test/fitnesse, donc une édition faite dans le navigateur est
        // écrite dans un fichier suivi par Git. Seuls l'historique FitNesse,
        // les captures et le log sont générés (et ignorés) ailleurs.
        Path wiki = Path.of("src", "test", "fitnesse");
        Path log = Path.of("target", "fitnesse-run.log");

        // FitNesse tourne dans sa propre JVM : sinon ses dépendances (Velocity,
        // Groovy) entrent en conflit avec les classloaders de QuarkusTest.
        // Le classpath Selenium est fourni au serveur Slim par la page SetUp
        // (!path target/fitnesse/lib/*), pas par ce process.
        List<String> commande = List.of(
                System.getProperty("java.home") + "/bin/java",
                "-Dslim.port=8130",
                // Sinon FitNesse recopie tout son wiki par défaut (documentation,
                // suites d'acceptation) dans notre répertoire de travail.
                "-DOmittingUpdates=true",
                "-cp", System.getProperty("java.class.path"),
                "fitnesseMain.FitNesseMain",
                "-d", wiki.toString(),
                "-p", "8127");

        Process fitNesse = new ProcessBuilder(commande)
                .redirectOutput(log.toFile())
                .redirectErrorStream(true)
                .start();
        try {
            attendreServeur();

            verifier("TaverneTests", "3", "les trois lignes de la table REST doivent passer");
            verifier("TaverneUiTests", "1", "la vérification IHM doit passer");
            verifier("ReglesTarifTests", "3", "les trois règles de tarification doivent passer");
            verifier("ScenarioTests", "2", "les deux profils du scénario doivent être accueillis");
            verifier("ApiContratTests", "4", "les quatre assertions du contrat HTTP doivent passer");
        } finally {
            fitNesse.destroyForcibly();
            fitNesse.waitFor();
        }
    }

    private void verifier(String page, String right, String message) throws Exception {
        Element resultat = counts(page);
        assertEquals("0", texte(resultat, "wrong"), message + " : aucune assertion ne doit être fausse");
        assertEquals("0", texte(resultat, "exceptions"), message + " : aucune exception ne doit survenir");
        assertEquals(right, texte(resultat, "right"), message);
    }

    private Element counts(String page) throws Exception {
        String xml = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(FITNESSE + "/" + page + "?responder=test&format=xml"))
                        .timeout(Duration.ofSeconds(60))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString()).body();

        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        return (Element) document.getElementsByTagName("counts").item(0);
    }

    private void attendreServeur() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        for (int i = 0; i < 40; i++) {
            try {
                HttpResponse<String> response = client.send(
                        HttpRequest.newBuilder(URI.create(FITNESSE + "/"))
                                .timeout(Duration.ofSeconds(1))
                                .GET().build(),
                        HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return;
                }
            } catch (Exception ignored) {
                // Serveur pas encore démarré.
            }
            Thread.sleep(500);
        }
        throw new IllegalStateException("FitNesse n'a pas démarré sur le port 8127");
    }

    private String texte(Element parent, String tag) {
        return ((Element) parent.getElementsByTagName(tag).item(0)).getTextContent();
    }
}
