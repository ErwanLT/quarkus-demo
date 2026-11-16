package fr.eletutour.tavern.coursier.client;

import fr.eletutour.tavern.coursier.exception.ContratIntrouvableException;
import fr.eletutour.tavern.coursier.model.Contrat;
import fr.eletutour.tavern.coursier.model.ContratAccepte;
import fr.eletutour.tavern.coursier.model.Difficulte;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class GuildeClientTest {

    @Inject
    @RestClient
    GuildeClient guilde;

    @Test
    @DisplayName("Le client déclaratif récupère le tableau des contrats")
    void listeLesContrats() {
        List<Contrat> contrats = guilde.contrats(null);

        assertEquals(3, contrats.size());
        assertEquals("Rats dans la cave", contrats.getFirst().titre());
    }

    @Test
    @DisplayName("Le query parameter filtre les contrats par difficulté")
    void filtreParDifficulte() {
        List<Contrat> contrats = guilde.contrats(Difficulte.MORTEL);

        assertEquals(1, contrats.size());
        assertEquals(3, contrats.getFirst().id());
        assertEquals("Le dragon de la combe", contrats.getFirst().titre());
    }

    @Test
    @DisplayName("Le header X-Coursier est transmis lors de la signature")
    void accepteUnContrat() {
        ContratAccepte accepte = guilde.accepter(2, "Brune la rôdeuse");

        assertEquals(2, accepte.id());
        assertEquals("Brune la rôdeuse", accepte.coursier());
        assertEquals(20, accepte.recompense());
    }

    @Test
    @DisplayName("Un contrat inconnu devient une exception métier")
    void contratInconnuDevientUneExceptionMetier() {
        ContratIntrouvableException exception =
                assertThrows(ContratIntrouvableException.class, () -> guilde.contrat(99));

        assertTrue(exception.getMessage().contains("Guilde"));
    }
}
