package fr.eletutour.fitnesse.domain;

import java.util.List;

/**
 * Détail d'un prix : le montant avant remise, les remises appliquées
 * (dans l'ordre) et le total à payer, en pièces d'or.
 */
public record Devis(Chambre chambre, int nuits, boolean membre, int prixDeBase, List<String> remises, int total) {

    public String remisesLisibles() {
        return remises.isEmpty() ? "aucune" : String.join(" puis ", remises);
    }
}
