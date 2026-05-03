package fr.eletutour.database.panache.repository;

import fr.eletutour.database.panache.model.Recipe;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository pour l'entité Recipe.
 * Ce pattern permet de séparer la logique de persistance du modèle de données.
 */
@ApplicationScoped
public class RecipeRepository implements PanacheRepository<Recipe> {

    /**
     * Exemple de recherche personnalisée
     */
    public List<Recipe> findByTitle(String title) {
        return find("from Recipe r left join fetch r.ingredients where r.title like ?1", "%" + title + "%").list();
    }

    /**
     * Récupère toutes les recettes avec leurs ingrédients en une seule requête (évite le N+1)
     */
    public List<Recipe> listAllWithIngredients() {
        return find("from Recipe r left join fetch r.ingredients").list();
    }
}
