package fr.eletutour.tavern.editique.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Garde-fous de la charte graphique, vérifiés sur les sources : aucun rapport ne doit
 * reprendre la main sur une police, une couleur ou un format. Si quelqu'un ajoute un
 * {@code forecolor="#FF0000"} dans un .jrxml, c'est ce test qui le lui dira, pas le client.
 * <p>
 * Pas besoin de Quarkus ici : on lit simplement les fichiers XML.
 * </p>
 */
class RapportsSousCharteTest {

    private static final Path SOURCES = Path.of("src/main/jasperreports");

    /** Les attributs de présentation réservés à la charte. La mise en page (x, y, alignements) reste au rapport. */
    private static final Set<String> ATTRIBUTS_RESERVES = Set.of(
            "fontName", "fontSize", "forecolor", "backcolor", "bold", "italic", "underline", "pattern", "mode");

    /** Rapport → charte qu'il charge par défaut. */
    private static Map<Path, String> rapports() throws IOException {
        Map<Path, String> rapports = new LinkedHashMap<>();
        try (Stream<Path> fichiers = Files.list(SOURCES)) {
            for (Path rapport : fichiers.filter(f -> f.toString().endsWith(".jrxml")).sorted().toList()) {
                rapports.put(rapport, charteParDefaut(lire(rapport)));
            }
        }
        assertEquals(7, rapports.size(), "nombre de rapports inattendu : " + rapports.keySet());
        return rapports;
    }

    @Test
    @DisplayName("Aucun rapport ne déclare de style local, de police, de couleur ou de format en dur")
    void aucuneMiseEnFormeEnDur() throws IOException {
        List<String> ecarts = new ArrayList<>();
        for (Path rapport : rapports().keySet()) {
            Document document = lire(rapport);
            if (document.getElementsByTagName("style").getLength() > 0) {
                ecarts.add(rapport.getFileName() + " : style local <style>");
            }
            NodeList elements = document.getElementsByTagName("element");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                for (String attribut : ATTRIBUTS_RESERVES) {
                    if (element.hasAttribute(attribut)) {
                        ecarts.add(rapport.getFileName() + " : " + attribut + "=\"" + element.getAttribute(attribut)
                                + "\" sur un élément " + element.getAttribute("kind"));
                    }
                }
            }
            // Les traits (épaisseur, couleur, pointillés) sont eux aussi l'affaire de la charte
            if (document.getElementsByTagName("pen").getLength() > 0) {
                ecarts.add(rapport.getFileName() + " : trait <pen> défini dans le rapport");
            }
        }
        assertTrue(ecarts.isEmpty(), () -> "mise en forme en dur :\n" + String.join("\n", ecarts));
    }

    @Test
    @DisplayName("Chaque rapport, sous-rapports compris, charge sa charte par le paramètre CHARTE")
    void chaqueRapportChargeLaCharte() throws IOException {
        for (Map.Entry<Path, String> rapport : rapports().entrySet()) {
            Document document = lire(rapport.getKey());
            NodeList templates = document.getElementsByTagName("template");
            assertEquals(1, templates.getLength(), () -> rapport.getKey() + " : un seul <template> attendu");
            assertEquals("$P{CHARTE}", templates.item(0).getTextContent().trim(),
                    () -> rapport.getKey() + " : la charte doit venir du paramètre CHARTE");
            assertTrue(Files.exists(SOURCES.resolve(Path.of(rapport.getValue()).getFileName())),
                    () -> rapport.getKey() + " : charte par défaut introuvable : " + rapport.getValue());
        }
    }

    @Test
    @DisplayName("Chaque style employé par un rapport existe bien dans sa charte")
    void chaqueStyleExisteDansLaCharte() throws IOException {
        // Une faute de frappe dans un nom de style passe la compilation : elle n'éclate qu'au remplissage
        List<String> inconnus = new ArrayList<>();
        for (Map.Entry<Path, String> rapport : rapports().entrySet()) {
            Set<String> disponibles = stylesDe(SOURCES.resolve(Path.of(rapport.getValue()).getFileName()));
            NodeList elements = lire(rapport.getKey()).getElementsByTagName("element");
            for (int i = 0; i < elements.getLength(); i++) {
                String style = ((Element) elements.item(i)).getAttribute("style");
                if (!style.isEmpty() && !disponibles.contains(style)) {
                    inconnus.add(rapport.getKey().getFileName() + " : " + style);
                }
            }
        }
        assertTrue(inconnus.isEmpty(), () -> "styles absents de la charte :\n" + String.join("\n", inconnus));
    }

    @Test
    @DisplayName("La charte thermique reprend toute la charte de la taverne et n'en redéfinit qu'une partie")
    void charteThermiqueHerite() throws IOException {
        Document thermique = lire(SOURCES.resolve("charte-thermique.jrtx"));
        assertEquals("jasperreports/charte-taverne.jrtx",
                thermique.getElementsByTagName("template").item(0).getTextContent().trim());

        Set<String> taverne = stylesDe(SOURCES.resolve("charte-taverne.jrtx"));
        Set<String> redefinis = new TreeSet<>(stylesPropres(thermique));
        redefinis.retainAll(taverne);
        assertFalse(redefinis.isEmpty(), "la charte thermique ne redéfinit rien");
        assertTrue(redefinis.contains("Base"), "la charte thermique doit redéfinir le style par défaut");
        assertTrue(redefinis.size() < taverne.size(), "la charte thermique recopie toute la charte");
    }

    /** Styles d'une charte, y compris ceux des chartes qu'elle inclut. */
    private static Set<String> stylesDe(Path charte) throws IOException {
        Document document = lire(charte);
        Set<String> styles = new TreeSet<>(stylesPropres(document));
        NodeList inclusions = document.getElementsByTagName("template");
        for (int i = 0; i < inclusions.getLength(); i++) {
            styles.addAll(stylesDe(SOURCES.resolve(Path.of(inclusions.item(i).getTextContent().trim()).getFileName())));
        }
        return styles;
    }

    private static Set<String> stylesPropres(Document charte) {
        Set<String> styles = new TreeSet<>();
        NodeList noeuds = charte.getElementsByTagName("style");
        for (int i = 0; i < noeuds.getLength(); i++) {
            styles.add(((Element) noeuds.item(i)).getAttribute("name"));
        }
        return styles;
    }

    private static String charteParDefaut(Document rapport) {
        NodeList parametres = rapport.getElementsByTagName("parameter");
        for (int i = 0; i < parametres.getLength(); i++) {
            Element parametre = (Element) parametres.item(i);
            if ("CHARTE".equals(parametre.getAttribute("name")) && parametre.hasAttribute("class")) {
                return parametre.getTextContent().trim().replace("\"", "");
            }
        }
        throw new AssertionError("paramètre CHARTE absent de " + rapport.getDocumentElement().getAttribute("name"));
    }

    private static Document lire(Path fichier) throws IOException {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fichier.toFile());
        } catch (Exception e) {
            throw new IOException("XML illisible : " + fichier, e);
        }
    }
}
