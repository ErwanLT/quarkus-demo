package fr.eletutour.taverne.commis.domain;

import java.math.BigDecimal;

/**
 * Represente la dette d'un aventurier envers la taverne.
 *
 * @param nomAventurier nom de l'aventurier endette
 * @param montant       montant du en pieces d'or
 * @param joursDeRetard nombre de jours ecoules depuis l'echeance
 */
public record DetteAventurier(String nomAventurier, BigDecimal montant, int joursDeRetard) {

    /**
     * Indique si la dette est consideree comme critique, c'est-a-dire
     * suffisamment ancienne pour justifier une relance immediate.
     *
     * @return {@code true} si le retard depasse 30 jours
     */
    public boolean estCritique() {
        return joursDeRetard > 30;
    }
}
