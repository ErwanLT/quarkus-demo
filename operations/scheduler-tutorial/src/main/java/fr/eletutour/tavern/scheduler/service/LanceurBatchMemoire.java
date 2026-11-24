package fr.eletutour.tavern.scheduler.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/** Implémentation autonome du pont : elle journalise et compte les lancements. */
@ApplicationScoped
public class LanceurBatchMemoire implements LanceurBatch {

    private static final Logger LOG = Logger.getLogger(LanceurBatchMemoire.class);

    private final AtomicInteger lancements = new AtomicInteger();

    @Override
    public void lancerBrassageNocturne() {
        LOG.info("Le valet réveille l'intendant : lancement du job de brassage nocturne.");
        lancements.incrementAndGet();
    }

    public int lancements() {
        return lancements.get();
    }
}
