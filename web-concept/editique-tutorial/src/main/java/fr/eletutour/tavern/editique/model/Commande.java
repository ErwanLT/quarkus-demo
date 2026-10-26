package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Une commande passée à une table de la taverne, source du ticket de caisse.
 */
public class Commande {

    private final String numero;
    private final String table;
    private final String serveur;
    private final LocalDateTime date;
    private final List<LigneCommande> lignes;
    private final List<Paiement> paiements;

    public Commande(String numero, String table, String serveur, LocalDateTime date, List<LigneCommande> lignes) {
        this(numero, table, serveur, date, lignes, List.of());
    }

    public Commande(String numero, String table, String serveur, LocalDateTime date,
                    List<LigneCommande> lignes, List<Paiement> paiements) {
        this.numero = numero;
        this.table = table;
        this.serveur = serveur;
        this.date = date;
        this.lignes = List.copyOf(lignes);
        this.paiements = List.copyOf(paiements);
    }

    public String getNumero() {
        return numero;
    }

    public String getTable() {
        return table;
    }

    public String getServeur() {
        return serveur;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public BigDecimal getTotal() {
        return lignes.stream()
                .map(LigneCommande::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
