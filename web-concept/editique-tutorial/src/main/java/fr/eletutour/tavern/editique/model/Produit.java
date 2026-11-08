package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;

/**
 * Un produit de la taverne, destiné à recevoir une étiquette scannable.
 * <p>
 * Le code EAN-13 est stocké sous forme de base de 12 chiffres : la clé de contrôle
 * est calculée par {@code IdentificationService}, jamais recopiée à la main.
 * </p>
 */
public class Produit {

    private final String reference;
    private final String designation;
    private final BigDecimal prixUnitaire;
    private final String unite;
    private final String codeEan13Base;

    public Produit(String reference, String designation, BigDecimal prixUnitaire,
                   String unite, String codeEan13Base) {
        this.reference = reference;
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.unite = unite;
        this.codeEan13Base = codeEan13Base;
    }

    public String getReference() {
        return reference;
    }

    public String getDesignation() {
        return designation;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public String getUnite() {
        return unite;
    }

    public String getCodeEan13Base() {
        return codeEan13Base;
    }
}
