package fr.eletutour.tavern.scheduler.resource.dto;

import io.quarkus.scheduler.Trigger;

import java.time.Instant;

/** Vue REST d'un déclenchement enregistré par le scheduler. */
public record JobPlanifie(
        String identite,
        Instant prochaineExecution,
        Instant precedenteExecution,
        boolean enRetard) {

    public static JobPlanifie depuis(Trigger trigger) {
        return new JobPlanifie(
                trigger.getId(),
                trigger.getNextFireTime(),
                trigger.getPreviousFireTime(),
                trigger.isOverdue());
    }
}
