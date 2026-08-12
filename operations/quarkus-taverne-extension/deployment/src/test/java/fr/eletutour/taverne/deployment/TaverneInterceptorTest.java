package fr.eletutour.taverne.deployment;

import fr.eletutour.taverne.runtime.Taverne;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * L'épreuve de trempe : on vérifie que la relique fonctionne réellement,
 * pas seulement qu'elle compile.
 *
 * <p>{@code QuarkusUnitTest} boote une application Quarkus minimale, à blanc,
 * uniquement pour tester l'extension elle-même — c'est différent d'un test
 * applicatif classique.
 */
public class TaverneInterceptorTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(QuestServiceDeTest.class));

    /**
     * Les annonces captées au vol pendant le test. On garde une référence forte
     * sur le logger : {@code java.util.logging} ne retient les siens que
     * faiblement, et un logger ramassé par le GC emporterait notre handler.
     */
    private static final List<LogRecord> ANNONCES = new ArrayList<>();
    private static Logger loggerDeLaTaverne;
    private static Handler oreilleIndiscrete;

    @Inject
    QuestServiceDeTest questService;

    @BeforeAll
    static void ecouterLaTaverne() {
        oreilleIndiscrete = new Handler() {
            @Override
            public void publish(LogRecord record) {
                ANNONCES.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        loggerDeLaTaverne = Logger.getLogger("la-taverne");
        loggerDeLaTaverne.addHandler(oreilleIndiscrete);
    }

    @AfterAll
    static void quitterLaTaverne() {
        loggerDeLaTaverne.removeHandler(oreilleIndiscrete);
    }

    @BeforeEach
    void faireSilence() {
        ANNONCES.clear();
    }

    @Test
    @DisplayName("la quête renvoie son résultat intact, l'intercepteur ne le dénature pas")
    void laQueteDoitEtreAccomplie() {
        String resultat = questService.partirEnQuete();

        Assertions.assertEquals("Le dragon est vaincu", resultat);
    }

    /**
     * Le test qui compte vraiment. {@code laQueteDoitEtreAccomplie} passerait au
     * vert même si l'intercepteur ne se déclenchait jamais, puisque
     * {@code context.proceed()} renvoie de toute façon le résultat d'origine :
     * c'est exactement ce qui masque un {@code @Nonbinding} oublié. Ici on
     * vérifie que le sortilège a été prononcé, pas que la quête a abouti.
     */
    @Test
    @DisplayName("le tavernier annonce réellement la quête, en citant son nom")
    void lInterceptorDoitReellementParler() {
        questService.partirEnQuete();

        boolean sortilegeProclame = ANNONCES.stream()
                .map(TaverneInterceptorTest::rendre)
                .anyMatch(message -> message.contains("Terrasser le dragon"));

        Assertions.assertTrue(sortilegeProclame,
                "l'intercepteur n'a laissé aucune trace, le sortilège n'a pas été prononcé");
    }

    /**
     * {@code LOG.infof(...)} ne formate pas tout de suite : le {@code LogRecord}
     * porte le motif d'un côté et les paramètres de l'autre. Se contenter de
     * {@code getMessage()} ne verrait jamais le nom de la quête.
     */
    private static String rendre(LogRecord record) {
        Object[] parametres = record.getParameters();
        if (parametres == null || parametres.length == 0) {
            return record.getMessage();
        }
        return String.format(record.getMessage(), parametres);
    }

    @ApplicationScoped
    public static class QuestServiceDeTest {

        @Taverne(quete = "Terrasser le dragon")
        public String partirEnQuete() {
            return "Le dragon est vaincu";
        }
    }
}
