package fr.eletutour.tavern.federation.aventurier.service;

import fr.eletutour.tavern.federation.aventurier.model.Aventurier;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AventurierService {

    private final List<Aventurier> aventuriers = new ArrayList<>();

    public List<Aventurier> getAllAventuriers() {
        if (aventuriers.isEmpty()) {
            initData();
        }
        return List.copyOf(aventuriers);
    }

    public Aventurier getAventurier(Long id) {
        return getAllAventuriers().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void initData() {
        aventuriers.add(new Aventurier(1L, "Baldric", "Guerrier", 12, 340));
        aventuriers.add(new Aventurier(2L, "Sylvane", "Archère", 9, 120));
        aventuriers.add(new Aventurier(3L, "Grendel", "Nain", 15, 610));
        aventuriers.add(new Aventurier(4L, "Ysolde", "Prêtresse", 7, 95));
        aventuriers.add(new Aventurier(5L, "Thane", "Rôdeur", 11, 275));
        aventuriers.add(new Aventurier(6L, "Odran", "Barde", 6, 40));
        aventuriers.add(new Aventurier(7L, "Freya", "Paladin", 18, 890));
        aventuriers.add(new Aventurier(8L, "Corwin", "Mage", 9, 150));
    }
}
