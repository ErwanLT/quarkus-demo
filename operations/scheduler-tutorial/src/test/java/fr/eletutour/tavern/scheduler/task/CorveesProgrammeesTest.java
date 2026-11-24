package fr.eletutour.tavern.scheduler.task;

import fr.eletutour.tavern.scheduler.model.EtatCorvee;
import fr.eletutour.tavern.scheduler.model.TacheCorvee;
import fr.eletutour.tavern.scheduler.service.LanceurBatchMemoire;
import fr.eletutour.tavern.scheduler.service.RegistreDesCorvees;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class CorveesProgrammeesTest {

    @Inject
    CorveesProgrammees corvees;

    @Inject
    RegistreDesCorvees registre;

    @Inject
    LanceurBatchMemoire lanceurBatch;

    @Test
    @DisplayName("Une corvée forcée est comptée, sans retard, sur le fil de l'appelant")
    void enregistreLaPressionDesFuts() {
        long avant = registre.executions(TacheCorvee.PRESSION.identifiant());

        corvees.executer(TacheCorvee.PRESSION);

        assertEquals(avant + 1, registre.executions(TacheCorvee.PRESSION.identifiant()));
        EtatCorvee etat = etat(TacheCorvee.PRESSION);
        assertNotNull(etat.planifiee());
        assertEquals(etat.planifiee(), etat.reelle());
        assertEquals(Thread.currentThread().getName(), etat.fil());
    }

    @Test
    @DisplayName("Le registre conserve l'écart entre l'heure prévue et l'heure réelle")
    void conserveLeRetardDUnDeclenchement() {
        Instant prevue = Instant.parse("2026-11-23T06:30:00Z");
        Instant reelle = prevue.plusSeconds(42);

        registre.enregistrer("corvee-en-retard", prevue, reelle);

        EtatCorvee etat = registre.etats().stream()
                .filter(e -> e.tache().equals("corvee-en-retard"))
                .findFirst()
                .orElseThrow();
        assertEquals(prevue, etat.planifiee());
        assertEquals(reelle, etat.reelle());
    }

    @Test
    @DisplayName("Les verres vides peuvent passer plusieurs fois sans s'écraser")
    void comptePlusieursPassagesDesVerres() {
        long avant = registre.executions(TacheCorvee.VERRES.identifiant());

        corvees.executer(TacheCorvee.VERRES);
        corvees.executer(TacheCorvee.VERRES);

        assertEquals(avant + 2, registre.executions(TacheCorvee.VERRES.identifiant()));
    }

    @Test
    @DisplayName("Forcée à la main, la corvée @NonBlocking tourne sur le fil de l'appelant")
    void executeLaCorveeNonBloquante() {
        long avant = registre.executions(TacheCorvee.COMPTOIR.identifiant());

        corvees.executer(TacheCorvee.COMPTOIR);

        assertEquals(avant + 1, registre.executions(TacheCorvee.COMPTOIR.identifiant()));
        assertEquals(Thread.currentThread().getName(), etat(TacheCorvee.COMPTOIR).fil());
    }

    @Test
    @DisplayName("Le balayage de l'aube est journalisé")
    void executeLeBalayage() {
        long avant = registre.executions(TacheCorvee.BALAYAGE.identifiant());

        corvees.executer(TacheCorvee.BALAYAGE);

        assertEquals(avant + 1, registre.executions(TacheCorvee.BALAYAGE.identifiant()));
    }

    @Test
    @DisplayName("Le réveil de 3h00 déclenche le batch et journalise la corvée")
    void reveilleLIntendant() {
        long avant = registre.executions(TacheCorvee.INTENDANT.identifiant());
        int lancementsAvant = lanceurBatch.lancements();

        corvees.executer(TacheCorvee.INTENDANT);

        assertEquals(avant + 1, registre.executions(TacheCorvee.INTENDANT.identifiant()));
        assertEquals(lancementsAvant + 1, lanceurBatch.lancements());
    }

    private EtatCorvee etat(TacheCorvee tache) {
        return registre.etats().stream()
                .filter(etat -> etat.tache().equals(tache.identifiant()))
                .findFirst()
                .orElseThrow();
    }
}
