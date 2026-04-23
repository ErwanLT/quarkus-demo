package fr.eletutour.tavern.batch.scheduler;

import jakarta.batch.operations.JobOperator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Properties;
import org.jboss.logging.Logger;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class NightlyBrewScheduler {

    private static final Logger LOG = Logger.getLogger(NightlyBrewScheduler.class);
    private static final String JOB_NAME = "nightly-brew-job";

    @Inject
    JobOperator jobOperator;

    @Scheduled(cron = "{tavern.brewery.nightly-cron}")
    void brewAtNight() {
        long executionId = jobOperator.start(JOB_NAME, new Properties());
        LOG.infov("Nightly brew job launched with executionId={0}", executionId);
    }
}
