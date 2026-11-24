package fr.eletutour.tavern.scheduler.task;

import fr.eletutour.tavern.scheduler.model.IdentifiantsCorvees;
import io.quarkus.scheduler.Scheduler;
import io.quarkus.scheduler.Trigger;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Vérifie l'enregistrement des corvées sans attendre la moindre échéance. */
@QuarkusTest
@TestProfile(SchedulerActifProfile.class)
class SchedulerActifTest {

    @Inject
    Scheduler scheduler;

    @Test
    @DisplayName("Les cinq corvées sont enregistrées avec une prochaine échéance")
    void enregistreLesCinqCorvees() {
        List<Trigger> jobs = scheduler.getScheduledJobs();

        assertEquals(5, jobs.size());
        assertEquals(
                List.of(
                        IdentifiantsCorvees.BALAYAGE,
                        IdentifiantsCorvees.COMPTOIR,
                        IdentifiantsCorvees.PRESSION,
                        IdentifiantsCorvees.INTENDANT,
                        IdentifiantsCorvees.VERRES),
                jobs.stream().map(Trigger::getId).sorted().toList());
        assertTrue(jobs.stream().allMatch(job -> job.getNextFireTime() != null));
        assertNotNull(scheduler.getScheduledJob(IdentifiantsCorvees.INTENDANT));
    }

    @Test
    @DisplayName("L'API expose les déclenchements enregistrés")
    void exposeLesTaches() {
        given()
                .when().get("/corvees/taches")
                .then()
                .statusCode(200)
                .body("identite", hasItems(
                        IdentifiantsCorvees.PRESSION,
                        IdentifiantsCorvees.VERRES,
                        IdentifiantsCorvees.BALAYAGE,
                        IdentifiantsCorvees.COMPTOIR,
                        IdentifiantsCorvees.INTENDANT));
    }

    @Test
    @DisplayName("Une corvée peut être mise en pause puis reprise")
    void pauseEtReprendUneCorvee() {
        assertFalse(scheduler.isPaused(IdentifiantsCorvees.PRESSION));
        try {
            scheduler.pause(IdentifiantsCorvees.PRESSION);
            assertTrue(scheduler.isPaused(IdentifiantsCorvees.PRESSION));
        } finally {
            scheduler.resume(IdentifiantsCorvees.PRESSION);
        }
        assertFalse(scheduler.isPaused(IdentifiantsCorvees.PRESSION));
    }
}
