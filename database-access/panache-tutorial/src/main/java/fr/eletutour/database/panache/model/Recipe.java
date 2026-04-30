package fr.eletutour.database.panache.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Recipe utilisant le pattern Repository.
 * L'entité reste un POJO JPA classique (avec ou sans extensions PanacheEntityBase).
 */
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String title;
    public String description;

    @ManyToMany
    public List<Ingredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
