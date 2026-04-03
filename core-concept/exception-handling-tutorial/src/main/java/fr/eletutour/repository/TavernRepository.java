package fr.eletutour.repository;

import fr.eletutour.model.Tavern;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class TavernRepository {
    private final Map<String, Tavern> taverns = new HashMap<>();

    public TavernRepository() {
        taverns.put("LePoneyFringant", new Tavern("LePoneyFringant", "Bree", 50));
        taverns.put("LeDragonVert", new Tavern("LeDragonVert", "La Comté", 30));
    }

    public Optional<Tavern> findByName(String name) {
        return Optional.ofNullable(taverns.get(name));
    }

    public void save(Tavern tavern) {
        taverns.put(tavern.getName(), tavern);
    }

    public boolean exists(String name) {
        return taverns.containsKey(name);
    }
}
