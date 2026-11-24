package fr.eletutour.tavern.scheduler.task;

import fr.eletutour.tavern.scheduler.model.IdentifiantsCorvees;
import fr.eletutour.tavern.scheduler.model.TacheCorvee;
import fr.eletutour.tavern.scheduler.service.LanceurBatch;
import fr.eletutour.tavern.scheduler.service.RegistreDesCorvees;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.common.annotation.NonBlocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;

import static io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP;

/**
 * Les corvées périodiques de la taverne.
 *
 * <p>Une méthode {@code @Scheduled} ne contient que l'orchestration : elle extrait
 * au besoin les horodatages de {@link ScheduledExecution}, puis délègue à une
 * méthode métier. {@link #executer(TacheCorvee)} appelle ces mêmes méthodes
 * métier : le code testé est celui qui tourne en dev et en prod.</p>
 */
@ApplicationScoped
public class CorveesProgrammees {

    @Inject
    RegistreDesCorvees registre;

    @Inject
    LanceurBatch lanceurBatch;

    /** Toutes les 10 minutes : exécution bloquante, le défaut du scheduler. */
    @Scheduled(identity = IdentifiantsCorvees.PRESSION, every = "${corvees.pression.every:10m}")
    void verifierPressionFuts(ScheduledExecution execution) {
        controlerPression(execution.getScheduledFireTime(), execution.getFireTime());
    }

    /** Toutes les heures : une seule occurrence à la fois dans cette instance. */
    @Scheduled(identity = IdentifiantsCorvees.VERRES, every = "${corvees.verres.every:1h}",
            concurrentExecution = SKIP)
    void ramasserLesVerresVides() {
        registre.enregistrer(TacheCorvee.VERRES.identifiant());
    }

    /** À l'aube : le cron Quartz, avec les horodatages prévu / réel. */
    @Scheduled(identity = IdentifiantsCorvees.BALAYAGE, cron = "${corvees.balayage.cron:0 30 6 * * ?}")
    void balayerLentree(ScheduledExecution execution) {
        balayer(execution.getScheduledFireTime(), execution.getFireTime());
    }

    /** Corvée légère et non bloquante : exécution sur la boucle d'événements Vert.x. */
    @Scheduled(identity = IdentifiantsCorvees.COMPTOIR, every = "${corvees.comptoir.every:30s}")
    @NonBlocking
    void nettoyerLeComptoir() {
        registre.enregistrer(TacheCorvee.COMPTOIR.identifiant());
    }

    /** Le pont avec le tutoriel batch : à 3 h 00, on réveille l'intendant. */
    @Scheduled(identity = IdentifiantsCorvees.INTENDANT, cron = "${corvees.batch.cron:0 0 3 * * ?}")
    void reveillerLIntendant() {
        lanceurBatch.lancerBrassageNocturne();
        registre.enregistrer(TacheCorvee.INTENDANT.identifiant());
    }

    /**
     * Déclenchement manuel, utilisé par l'API et les tests. Il s'exécute sur le fil
     * de l'appelant : un déclenchement forcé n'a ni retard, ni boucle d'événements.
     */
    public void executer(TacheCorvee tache) {
        Instant maintenant = Instant.now();
        switch (tache) {
            case PRESSION -> controlerPression(maintenant, maintenant);
            case VERRES -> ramasserLesVerresVides();
            case BALAYAGE -> balayer(maintenant, maintenant);
            case COMPTOIR -> nettoyerLeComptoir();
            case INTENDANT -> reveillerLIntendant();
        }
    }

    private void controlerPression(Instant planifiee, Instant reelle) {
        registre.enregistrer(TacheCorvee.PRESSION.identifiant(), planifiee, reelle);
    }

    private void balayer(Instant planifiee, Instant reelle) {
        registre.enregistrer(TacheCorvee.BALAYAGE.identifiant(), planifiee, reelle);
    }
}
