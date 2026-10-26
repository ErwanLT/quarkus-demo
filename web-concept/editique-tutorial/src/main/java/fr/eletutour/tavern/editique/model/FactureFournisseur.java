package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Facture reçue d'un fournisseur de la taverne.
 */
public class FactureFournisseur {

    private final String numero;
    private final Fournisseur fournisseur;
    private final LocalDate dateEmission;
    private final LocalDate dateEcheance;
    private final List<LigneFacture> lignes;

    public FactureFournisseur(String numero, Fournisseur fournisseur, LocalDate dateEmission,
                              LocalDate dateEcheance, List<LigneFacture> lignes) {
        this.numero = numero;
        this.fournisseur = fournisseur;
        this.dateEmission = dateEmission;
        this.dateEcheance = dateEcheance;
        this.lignes = List.copyOf(lignes);
    }

    public String getNumero() {
        return numero;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public List<LigneFacture> getLignes() {
        return lignes;
    }

    /**
     * Regroupe les lignes par taux de TVA, trié par taux croissant.
     */
    public List<LigneTva> getRecapTva() {
        Map<BigDecimal, BigDecimal> basesParTaux = lignes.stream()
                .collect(Collectors.groupingBy(LigneFacture::getTauxTva, TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, LigneFacture::getMontantHt, BigDecimal::add)));
        return basesParTaux.entrySet().stream()
                .map(e -> new LigneTva(e.getKey(), e.getValue(),
                        e.getValue().multiply(e.getKey()).setScale(2, RoundingMode.HALF_UP)))
                .toList();
    }

    public BigDecimal getTotalHt() {
        return lignes.stream()
                .map(LigneFacture::getMontantHt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTva() {
        return getRecapTva().stream()
                .map(LigneTva::getMontantTva)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTtc() {
        return getTotalHt().add(getTotalTva());
    }
}
