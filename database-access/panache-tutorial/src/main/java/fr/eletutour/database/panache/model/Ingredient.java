package fr.eletutour.database.panache.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

/**
 * Entité Ingredient.
 * Utilise PanacheEntity pour bénéficier de l'ID automatique et des méthodes Active Record.
 */
@Entity
public class Ingredient extends PanacheEntity {

    public String name;
    public String unit;
    public double cost;

    public Ingredient() {
    }

    public Ingredient(String name, String unit, double cost) {
        this.name = name;
        this.unit = unit;
        this.cost = cost;
    }
}
