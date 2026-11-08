package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;

/**
 * Données prêtes à imprimer sur une étiquette : Java a déjà calculé le code
 * EAN-13 complet, le rapport n'a plus qu'à le dessiner.
 */
public class EtiquetteProduit {

    private final String reference;
    private final String designation;
    private final BigDecimal prixUnitaire;
    private final String unite;
    private final String ean13;

    public EtiquetteProduit(String reference, String designation, BigDecimal prixUnitaire,
                            String unite, String ean13) {
        this.reference = reference;
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.unite = unite;
        this.ean13 = ean13;
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

    public String getEan13() {
        return ean13;
    }
}
