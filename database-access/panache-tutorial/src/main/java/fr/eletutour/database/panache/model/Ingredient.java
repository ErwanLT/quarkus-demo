package fr.eletutour.database.panache.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

/**
 * Entité Ingredient utilisant le pattern Active Record de Panache.
 * Dans ce pattern, l'entité elle-même contient les méthodes d'accès aux données.
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

    /**
     * Exemple de méthode personnalisée dans le pattern Active Record
     */
    public static Ingredient findByName(String name) {
        return find("name", name).firstResult();
    }
}
