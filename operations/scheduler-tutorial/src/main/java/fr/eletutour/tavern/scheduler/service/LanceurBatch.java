package fr.eletutour.tavern.scheduler.service;

/**
 * Le pont avec le tutoriel batch. Dans une vraie application, l'implémentation
 * injecte {@code jakarta.batch.operations.JobOperator} et appelle
 * {@code jobOperator.start("nightly-brew-job", new Properties())}.
 */
public interface LanceurBatch {

    void lancerBrassageNocturne();
}
