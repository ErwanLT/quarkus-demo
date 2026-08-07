package fr.eletutour.tavern.federation.quete.service;

import fr.eletutour.tavern.federation.quete.model.Quete;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class QueteService {

    private final List<Quete> quetes = new ArrayList<>();

    public List<Quete> getAll() {
        if (quetes.isEmpty()) {
            initData();
        }
        return List.copyOf(quetes);
    }

    /**
     * Regroupe les quêtes de plusieurs aventuriers en un seul passage,
     * pour le resolver batché de QueteResource.
     */
    public Map<Long, List<Quete>> getQuetesForAventuriers(List<Long> aventurierIds) {
        return getAll().stream()
                .filter(q -> aventurierIds.contains(q.aventurierId))
                .collect(Collectors.groupingBy(q -> q.aventurierId));
    }

    private void initData() {
        // Pas de quête pour Odran (id 6) : sert à vérifier que le batching
        // renvoie bien une liste vide plutôt qu'une entrée manquante.
        quetes.add(quete(100L, "Nettoyer les caves infestées", "Moyenne", 250, 1L));
        quetes.add(quete(101L, "Escorter la caravane des marchands", "Facile", 80, 2L));
        quetes.add(quete(102L, "Rouvrir la mine effondrée", "Difficile", 400, 3L));
        quetes.add(quete(103L, "Bénir le puits empoisonné", "Facile", 60, 4L));
        quetes.add(quete(104L, "Traquer le loup-garou de la forêt noire", "Difficile", 500, 5L));
        quetes.add(quete(105L, "Purifier le sanctuaire profané", "Difficile", 700, 7L));
        quetes.add(quete(106L, "Stabiliser le portail instable", "Moyenne", 220, 8L));
    }

    private Quete quete(Long id, String titre, String difficulte, Integer recompenseOr, Long aventurierId) {
        Quete q = new Quete();
        q.id = id;
        q.titre = titre;
        q.difficulte = difficulte;
        q.recompenseOr = recompenseOr;
        q.aventurierId = aventurierId;
        return q;
    }
}
