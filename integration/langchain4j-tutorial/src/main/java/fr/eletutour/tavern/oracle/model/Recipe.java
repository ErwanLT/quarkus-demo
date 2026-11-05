package fr.eletutour.tavern.oracle.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Recipe extends PanacheEntity {
    public String name;
    public String description;
    public String ingredients;

    /** Ligne du grimoire transmise au modèle, que ce soit par l'outil ou par le retrieval. */
    public String toGrimoireLine() {
        return name + " — " + description + " (ingrédients : " + ingredients + ")";
    }
}
