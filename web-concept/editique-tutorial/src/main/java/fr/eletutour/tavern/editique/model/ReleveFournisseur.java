package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Les factures d'un fournisseur regroupées pour le relevé mensuel.
 */
public class ReleveFournisseur {

    private final Fournisseur fournisseur;
    private final List<FactureFournisseur> factures;

    public ReleveFournisseur(Fournisseur fournisseur, List<FactureFournisseur> factures) {
        this.fournisseur = fournisseur;
        this.factures = List.copyOf(factures);
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public List<FactureFournisseur> getFactures() {
        return factures;
    }

    public Integer getNombreFactures() {
        return factures.size();
    }

    public BigDecimal getTotalTtc() {
        return factures.stream()
                .map(FactureFournisseur::getTotalTtc)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
