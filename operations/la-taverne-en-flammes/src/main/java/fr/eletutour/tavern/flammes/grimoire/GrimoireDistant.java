package fr.eletutour.tavern.flammes.grimoire;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Le grimoire de la guilde, quelque part a l'autre bout du royaume.
 *
 * <p>Dependance externe simulee : on peut regler sa latence et le mettre en panne pour
 * declencher les protections {@code @Timeout}, {@code @Retry} et {@code @Fallback}.</p>
 */
@ApplicationScoped
public class GrimoireDistant {

    private static final Logger LOG = Logger.getLogger(GrimoireDistant.class);

    private final AtomicLong latenceMs = new AtomicLong(50L);
    private final AtomicBoolean enPanne = new AtomicBoolean(false);
    private final AtomicInteger tentatives = new AtomicInteger();

    public long latenceMs() {
        return latenceMs.get();
    }

    public void reglerLatence(long millisecondes) {
        latenceMs.set(Math.max(0L, millisecondes));
        LOG.infof("Latence du grimoire distant reglee a %sms", latenceMs.get());
    }

    public boolean enPanne() {
        return enPanne.get();
    }

    public void basculerPanne(boolean active) {
        enPanne.set(active);
        LOG.infof("Grimoire distant %s", active ? "hors service" : "de nouveau joignable");
    }

    public int tentatives() {
        return tentatives.get();
    }

    public int reinitialiserTentatives() {
        return tentatives.getAndSet(0);
    }

    public String recuperer(String nom) {
        int tentative = tentatives.incrementAndGet();
        LOG.debugf("Appel du grimoire distant : nom=%s, tentative=%d", nom, tentative);
        attendre();

        if (enPanne.get()) {
            throw new IllegalStateException("Le grimoire de la guilde ne repond pas");
        }
        return "Recette de " + nom + " : trois mesures d'orge, une pincee de sel de mine, mijoter une heure.";
    }

    private void attendre() {
        try {
            Thread.sleep(latenceMs.get());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consultation du grimoire interrompue", interruptedException);
        }
    }
}
