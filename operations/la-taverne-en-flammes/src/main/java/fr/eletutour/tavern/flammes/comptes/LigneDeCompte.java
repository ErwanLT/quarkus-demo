package fr.eletutour.tavern.flammes.comptes;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Une ligne du Grand Livre des Comptes, le seul bien qu'on ne peut pas laisser bruler.
 */
@Entity
@Table(name = "ligne_de_compte")
public class LigneDeCompte extends PanacheEntity {

    @Column(nullable = false)
    public String aventurier;

    @Column(nullable = false)
    public String libelle;

    @Column(name = "montant_pieces_or", nullable = false)
    public int montantPiecesOr;

    @Column(name = "enregistre_le", nullable = false)
    public Instant enregistreLe;

    public LigneDeCompte() {
        // requis par Hibernate
    }

    public LigneDeCompte(String aventurier, String libelle, int montantPiecesOr) {
        this.aventurier = aventurier;
        this.libelle = libelle;
        this.montantPiecesOr = montantPiecesOr;
        this.enregistreLe = Instant.now();
    }
}
