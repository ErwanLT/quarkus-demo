package fr.eletutour.database.panache.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Recipe utilisant le pattern Repository.
 */
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String title;
    public String description;

    @ManyToMany
    @JoinTable(
        name = "RECIPE_INGREDIENT",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    public List<Ingredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
