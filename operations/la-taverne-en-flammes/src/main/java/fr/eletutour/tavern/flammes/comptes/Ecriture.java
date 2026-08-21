package fr.eletutour.tavern.flammes.comptes;

import java.time.Instant;

/**
 * Vue exposee d'une ligne du grand livre.
 */
public record Ecriture(Long id, String aventurier, String libelle, int montantPiecesOr, Instant enregistreLe) {

    static Ecriture de(LigneDeCompte ligne) {
        return new Ecriture(ligne.id, ligne.aventurier, ligne.libelle, ligne.montantPiecesOr, ligne.enregistreLe);
    }
}
