package fr.eletutour.taverne.deployment;

import fr.eletutour.taverne.runtime.Taverne;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import org.junit.jupiter.api.Assertions;
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

    @Inject
    QuestServiceDeTest questService;

    @Test
    void laQueteDoitEtreAccomplie() {
        String resultat = questService.partirEnQuete();
        Assertions.assertEquals("INTERCEPTED: Le dragon est vaincu", resultat);
    }

    @ApplicationScoped
    public static class QuestServiceDeTest {

        @Taverne(quete = "Terrasser le dragon")
        public String partirEnQuete() {
            return "Le dragon est vaincu";
        }
    }
}
