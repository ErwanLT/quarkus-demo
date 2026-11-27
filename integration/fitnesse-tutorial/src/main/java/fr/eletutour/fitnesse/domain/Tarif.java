package fr.eletutour.fitnesse.domain;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

/**
 * Règles de prix de la taverne. Classe sans dépendance à Quarkus : la
 * fixture FitNesse de la grille tarifaire l'instancie directement, sans HTTP.
 *
 * <ul>
 *     <li>prix de base = prix de la chambre × nombre de nuits ;</li>
 *     <li>membre de la guilde : -20 % ;</li>
 *     <li>séjour de 3 nuits ou plus : -10 %, appliqué après la remise membre ;</li>
 *     <li>le total est arrondi à la pièce la plus proche.</li>
 * </ul>
 */
@ApplicationScoped
public class Tarif {

    static final int NUITS_LONG_SEJOUR = 3;

    public Devis devis(Chambre chambre, int nuits, boolean membre) {
        if (nuits < 1) {
            throw new IllegalArgumentException("Il faut réserver au moins une nuit");
        }
        int prixDeBase = chambre.prixParNuit() * nuits;
        double prix = prixDeBase;
        List<String> remises = new ArrayList<>();
        if (membre) {
            prix *= 0.8;
            remises.add("membre -20 %");
        }
        if (nuits >= NUITS_LONG_SEJOUR) {
            prix *= 0.9;
            remises.add("long séjour -10 %");
        }
        return new Devis(chambre, nuits, membre, prixDeBase, List.copyOf(remises), (int) Math.round(prix));
    }
}
