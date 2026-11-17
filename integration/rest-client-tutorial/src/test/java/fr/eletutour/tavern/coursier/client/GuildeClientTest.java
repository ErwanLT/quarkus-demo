package fr.eletutour.tavern.coursier.client;

import fr.eletutour.tavern.coursier.exception.business.CoursierError;
import fr.eletutour.tavern.coursier.exception.business.CoursierException;
import fr.eletutour.tavern.coursier.model.Contrat;
import fr.eletutour.tavern.coursier.model.ContratAccepte;
import fr.eletutour.tavern.coursier.model.Difficulte;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class GuildeClientTest {

    @Inject
    @RestClient
    GuildeClient guilde;

    @Test
    @DisplayName("Le client déclaratif récupère le tableau des contrats (preuve que le sceau X-Taverne est envoyé)")
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
    @DisplayName("L'en-tête calculé X-Request-Id revient comme bordereau, différent à chaque course")
    void chaqueCourseALeSonBordereau() {
        ContratAccepte premiere = guilde.accepter(1, "Brune la rôdeuse");
        ContratAccepte seconde = guilde.accepter(1, "Brune la rôdeuse");

        assertDoesNotThrow(() -> UUID.fromString(premiere.bordereau()));
        assertDoesNotThrow(() -> UUID.fromString(seconde.bordereau()));
        assertNotEquals(premiere.bordereau(), seconde.bordereau());
    }

    @Test
    @DisplayName("Un 404 de la Guilde devient l'erreur métier CONTRAT_INTROUVABLE")
    void contratInconnuDevientUneExceptionMetier() {
        CoursierException exception =
                assertThrows(CoursierException.class, () -> guilde.contrat(99));

        assertEquals(CoursierError.CONTRAT_INTROUVABLE, exception.getError());
    }

    @Test
    @DisplayName("Un 500 de la Guilde devient REPONSE_INATTENDUE avec le statut distant")
    void erreurDistanteDevientReponseInattendue() {
        CoursierException exception =
                assertThrows(CoursierException.class, () -> guilde.contrat(13));

        assertEquals(CoursierError.REPONSE_INATTENDUE, exception.getError());
        assertEquals(500, exception.getDetails().get("statutDistant"));
        assertEquals("La Guilde", exception.getDetails().get("service"));
    }
}
