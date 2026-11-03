package fr.eletutour.database.migrations.flyway;

import fr.eletutour.database.migrations.comptes.Ecriture;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ligne_de_compte")
public class LigneDeCompteFlyway extends PanacheEntity {

    public static final String DEVISE_PIECE_OR = "PIECE_OR";

    @Column(nullable = false)
    public String aventurier;

    @Column(nullable = false)
    public String libelle;

    @Column(name = "montant_pieces_or", nullable = false)
    public int montantPiecesOr;

    @Column(nullable = false, length = 8)
    public String devise = DEVISE_PIECE_OR;

    @Column(name = "enregistre_le", nullable = false)
    public Instant enregistreLe;

    public LigneDeCompteFlyway() {
    }

    public LigneDeCompteFlyway(String aventurier, String libelle, int montantPiecesOr) {
        this.aventurier = aventurier;
        this.libelle = libelle;
        this.montantPiecesOr = montantPiecesOr;
        this.enregistreLe = Instant.now();
    }

    public Ecriture versEcriture() {
        return new Ecriture(id, aventurier, libelle, montantPiecesOr, devise, enregistreLe);
    }
}
