package fr.eletutour.fitnesse;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FitNesseAcceptanceTest {

    private static final String FITNESSE = "http://localhost:8127";
    private static final Path CAPTURES = Path.of("src", "test", "fitnesse", "FitNesseRoot", "files", "captures");

    /**
     * Nombre d'assertions vertes attendues par page : si une ligne disparaît
     * d'une table, ou si une page n'est plus exécutée, le build le voit.
     */
    private static final Map<String, Integer> PAGES_VERTES = new LinkedHashMap<>();

    static {
        PAGES_VERTES.put("SuiteSetUp", 1);
        PAGES_VERTES.put("AccueilDesVoyageurs", 8);
        PAGES_VERTES.put("CarteDuJour", 32);
        PAGES_VERTES.put("GrilleTarifaire", 43);
        PAGES_VERTES.put("ParcoursEnSalle", 17);
        PAGES_VERTES.put("ReservationAuComptoir", 27);
    }

    private static Process fitNesse;

    @BeforeAll
    static void demarrerFitNesse() throws Exception {
        // Les pages .wiki sont la source de vérité : FitNesse lit directement
        // src/test/fitnesse, donc une édition faite dans le navigateur est
        // écrite dans un fichier suivi par Git. Seuls l'historique, les
        // captures et le log sont générés (et ignorés).
        Path log = Path.of("target", "fitnesse-run.log");

        // FitNesse tourne dans sa propre JVM : sinon ses dépendances (Velocity,
        // Groovy) entrent en conflit avec les classloaders de QuarkusTest.
        // Le classpath Selenium est fourni au serveur Slim par la page
        // TaverneSuite (!path target/fitnesse/lib/*), pas par ce process.
        List<String> commande = List.of(
                System.getProperty("java.home") + "/bin/java",
                "-Dslim.port=8130",
                // Sinon FitNesse recopie tout son wiki par défaut (documentation,
                // suites d'acceptation) dans notre répertoire de travail.
                "-DOmittingUpdates=true",
                "-cp", System.getProperty("java.class.path"),
                "fitnesseMain.FitNesseMain",
                "-d", Path.of("src", "test", "fitnesse").toString(),
                "-p", "8127");

        fitNesse = new ProcessBuilder(commande)
                .redirectOutput(log.toFile())
                .redirectErrorStream(true)
                .start();
        attendreServeur();
    }

    @AfterAll
    static void arreterFitNesse() throws InterruptedException {
        if (fitNesse != null) {
            fitNesse.destroyForcibly();
            fitNesse.waitFor();
        }
    }

    @Test
    @DisplayName("La suite de la taverne est entièrement verte, démonstration d'échec exclue par son tag")
    void laSuiteEstVerte() throws Exception {
        Document resultats = executer("TaverneSuite?suite&format=xml&excludeSuiteFilter=demo-echec");

        Map<String, Element> compteurs = compteursParPage(resultats);
        assertEquals(PAGES_VERTES.keySet(), compteurs.keySet(),
                "la suite doit exécuter exactement ces pages, sans la démonstration d'échec");

        PAGES_VERTES.forEach((page, right) -> {
            Element compteur = compteurs.get(page);
            assertEquals("0", texte(compteur, "wrong"), page + " : aucune assertion ne doit être fausse");
            assertEquals("0", texte(compteur, "exceptions"), page + " : aucune exception ne doit survenir");
            assertEquals(String.valueOf(right), texte(compteur, "right"), page + " : assertions vertes");
        });
    }

    @Test
    @DisplayName("La page d'échec volontaire est rouge comme prévu et joint une capture d'écran")
    void laDemonstrationDEchecEstRougeAvecCapture() throws Exception {
        long capturesAvant = capturesDEchec();

        Document resultats = executer("TaverneSuite.EchecVolontaireAvecCapture?test&format=xml");

        Element compteur = compteursParPage(resultats).get("EchecVolontaireAvecCapture");
        assertEquals("1", texte(compteur, "wrong"), "le check du prix doit échouer");
        assertEquals("1", texte(compteur, "exceptions"), "la vérification IHM doit lever une exception");
        assertTrue(capturesDEchec() > capturesAvant, "une capture PNG de l'échec doit être écrite");
    }

    private static Document executer(String chemin) throws Exception {
        String xml = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(FITNESSE + "/" + chemin))
                        .timeout(Duration.ofMinutes(3))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString()).body();
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    /** Un élément {@code <result>} par page exécutée, SuiteSetUp compris. */
    private static Map<String, Element> compteursParPage(Document resultats) {
        Map<String, Element> compteurs = new LinkedHashMap<>();
        NodeList pages = resultats.getElementsByTagName("result");
        for (int i = 0; i < pages.getLength(); i++) {
            Element page = (Element) pages.item(i);
            compteurs.put(texte(page, "relativePageName"),
                    (Element) page.getElementsByTagName("counts").item(0));
        }
        return compteurs;
    }

    private static long capturesDEchec() throws IOException {
        if (!Files.isDirectory(CAPTURES)) {
            return 0;
        }
        try (Stream<Path> fichiers = Files.list(CAPTURES)) {
            return fichiers.filter(f -> f.getFileName().toString().startsWith("echec-")).count();
        }
    }

    private static void attendreServeur() throws Exception {
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

    private static String texte(Element parent, String tag) {
        return parent.getElementsByTagName(tag).item(0).getTextContent();
    }
}
