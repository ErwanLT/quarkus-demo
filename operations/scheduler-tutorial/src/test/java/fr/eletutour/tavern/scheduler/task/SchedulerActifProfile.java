package fr.eletutour.tavern.scheduler.task;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/**
 * Active le scheduler mais le garde en mode {@code halted} : les tâches sont
 * bien enregistrées et exposées, mais aucune ne se déclenche pendant le test.
 */
public class SchedulerActifProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.scheduler.enabled", "true",
                "quarkus.scheduler.start-mode", "halted");
    }
}
