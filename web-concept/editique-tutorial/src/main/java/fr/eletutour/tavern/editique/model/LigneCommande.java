package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;

/**
 * Une ligne de l'ardoise d'un client : ce qu'il a consommé au comptoir.
 * <p>
 * Classe à getters (et non record) : le {@code JRBeanCollectionDataSource}
 * de JasperReports lit les champs via les conventions JavaBean.
 */
public class LigneCommande {

    private final String libelle;
    private final Integer quantite;
    private final BigDecimal prixUnitaire;

    public LigneCommande(String libelle, Integer quantite, BigDecimal prixUnitaire) {
        this.libelle = libelle;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public String getLibelle() {
        return libelle;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public BigDecimal getMontant() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
}
