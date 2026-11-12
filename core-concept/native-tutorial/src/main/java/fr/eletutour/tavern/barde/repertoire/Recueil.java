package fr.eletutour.tavern.barde.repertoire;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Le recueil de ballades du barde.
 *
 * <p>Les parchemins (fichiers texte et JSON) vivent dans {@code src/main/resources/ballades}
 * et sont lus avec {@code getResourceAsStream}. Sur la JVM, ils sont dans le jar.
 * En natif, ils ne sont embarqués dans le binaire que s'ils sont déclarés dans
 * {@code quarkus.native.resources.includes}.</p>
 */
@ApplicationScoped
public class Recueil {

    private static final String DOSSIER = "ballades/";

    private final ObjectMapper objectMapper;

    public Recueil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Repertoire repertoire() {
        try (InputStream parchemin = ouvrir(DOSSIER + "repertoire.json")) {
            return objectMapper.readValue(parchemin, Repertoire.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Ballade ballade(String titre) {
        String chemin = DOSSIER + titre + ".txt";
        try (InputStream parchemin = ouvrir(chemin)) {
            String texte = new String(parchemin.readAllBytes(), StandardCharsets.UTF_8);
            List<String> couplets = Arrays.stream(texte.split("\\R\\R"))
                    .map(String::strip)
                    .filter(couplet -> !couplet.isEmpty())
                    .toList();
            return new Ballade(titre, couplets);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private InputStream ouvrir(String chemin) {
        InputStream parchemin = Thread.currentThread().getContextClassLoader().getResourceAsStream(chemin);
        if (parchemin == null) {
            throw new BalladeIntrouvableException(chemin);
        }
        return parchemin;
    }
}
