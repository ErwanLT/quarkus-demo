package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;

/**
 * Récapitulatif de TVA pour un taux donné, affiché en bas de facture.
 */
public class LigneTva {

    private final BigDecimal taux;
    private final BigDecimal baseHt;
    private final BigDecimal montantTva;

    public LigneTva(BigDecimal taux, BigDecimal baseHt, BigDecimal montantTva) {
        this.taux = taux;
        this.baseHt = baseHt;
        this.montantTva = montantTva;
    }

    public BigDecimal getTaux() {
        return taux;
    }

    public BigDecimal getBaseHt() {
        return baseHt;
    }

    public BigDecimal getMontantTva() {
        return montantTva;
    }
}
