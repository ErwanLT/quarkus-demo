package fr.eletutour.tavern.scheduler.service;

import fr.eletutour.tavern.scheduler.model.EtatCorvee;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** État observable des corvées : compteur et dernière exécution. */
@ApplicationScoped
public class RegistreDesCorvees {

    private final ConcurrentMap<String, EtatCorvee> etats = new ConcurrentHashMap<>();

    /** Corvée sans horodatage particulier : prévu et réel valent maintenant. */
    public void enregistrer(String tache) {
        Instant maintenant = Instant.now();
        enregistrer(tache, maintenant, maintenant);
    }

    /** Corvée qui connaît son heure prévue et son heure réelle de déclenchement. */
    public void enregistrer(String tache, Instant planifiee, Instant reelle) {
        etats.compute(tache, (cle, precedent) -> new EtatCorvee(
                cle,
                precedent == null ? 1 : precedent.executions() + 1,
                planifiee,
                reelle,
                Thread.currentThread().getName()));
    }

    public List<EtatCorvee> etats() {
        return etats.values().stream()
                .sorted(Comparator.comparing(EtatCorvee::tache))
                .toList();
    }

    public long executions(String tache) {
        EtatCorvee etat = etats.get(tache);
        return etat == null ? 0 : etat.executions();
    }
}
