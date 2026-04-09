package fr.eletutour.service;

import fr.eletutour.exception.business.TavernException;
import fr.eletutour.model.Tavern;
import fr.eletutour.model.TavernCreateRequest;
import fr.eletutour.repository.TavernRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TavernService {

    @Inject
    TavernRepository repository;

    public Tavern getTavern(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> TavernException.notFound(name));
    }

    public void createTavern(TavernCreateRequest request) {
        if (repository.exists(request.name())) {
            throw TavernException.alreadyExists(request.name());
        }
        repository.save(new Tavern(request.name(), request.city(), request.maxCapacity()));
    }

    public void enterTavern(String name, int visitors) {
        Tavern tavern = getTavern(name);
        if (tavern.getCurrentCapacity() + visitors > tavern.getMaxCapacity()) {
            throw TavernException.capacityReached(name, tavern.getCurrentCapacity(), tavern.getMaxCapacity());
        }
        tavern.enter(visitors);
    }
}
