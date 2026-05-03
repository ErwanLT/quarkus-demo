package fr.eletutour.database.panache.repository;

import fr.eletutour.database.panache.model.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IngredientRepository implements PanacheRepository<Ingredient> {
    
    public Ingredient findByName(String name) {
        return find("name", name).firstResult();
    }
}
