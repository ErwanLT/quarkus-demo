package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Une ligne de facture fournisseur. Le taux de TVA est exprimé en fraction (0.20 pour 20 %).
 */
public class LigneFacture {

    private final String reference;
    private final String designation;
    private final Integer quantite;
    private final String unite;
    private final BigDecimal prixUnitaireHt;
    private final BigDecimal tauxTva;

    public LigneFacture(String reference, String designation, Integer quantite, String unite,
                        BigDecimal prixUnitaireHt, BigDecimal tauxTva) {
        this.reference = reference;
        this.designation = designation;
        this.quantite = quantite;
        this.unite = unite;
        this.prixUnitaireHt = prixUnitaireHt;
        this.tauxTva = tauxTva;
    }

    public String getReference() {
        return reference;
    }

    public String getDesignation() {
        return designation;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public String getUnite() {
        return unite;
    }

    public BigDecimal getPrixUnitaireHt() {
        return prixUnitaireHt;
    }

    public BigDecimal getTauxTva() {
        return tauxTva;
    }

    public BigDecimal getMontantHt() {
        return prixUnitaireHt.multiply(BigDecimal.valueOf(quantite)).setScale(2, RoundingMode.HALF_UP);
    }
}
