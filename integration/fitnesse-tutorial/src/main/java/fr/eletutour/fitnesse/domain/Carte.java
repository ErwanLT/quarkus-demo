package fr.eletutour.fitnesse.domain;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Comparator;
import java.util.List;

/**
 * La carte du jour, triée par catégorie puis du moins cher au plus cher.
 */
@ApplicationScoped
public class Carte {

    private static final List<String> ORDRE_CATEGORIES = List.of("entrée", "plat", "dessert", "boisson");

    private static final List<Plat> PLATS = List.of(
            new Plat("Hydromel du nain", "boisson", 4),
            new Plat("Bière ambrée de la Comté", "boisson", 3),
            new Plat("Potion de vigueur", "boisson", 12),
            new Plat("Soupe à l'oignon du chaudron", "entrée", 5),
            new Plat("Ragoût de sanglier", "plat", 14),
            new Plat("Tourte aux champignons", "plat", 11),
            new Plat("Cuissot de griffon rôti", "plat", 25),
            new Plat("Tarte aux mûres sauvages", "dessert", 6));

    public List<Plat> plats() {
        return PLATS.stream()
                .sorted(Comparator.comparingInt((Plat p) -> ORDRE_CATEGORIES.indexOf(p.categorie()))
                        .thenComparingInt(Plat::prix))
                .toList();
    }

    public List<Plat> plats(String categorie) {
        if (categorie == null || categorie.isBlank()) {
            return plats();
        }
        return plats().stream().filter(p -> p.categorie().equalsIgnoreCase(categorie.trim())).toList();
    }
}
