package fr.eletutour.database.migrations.comptes;

import java.time.Instant;

public record Ecriture(Long id, String aventurier, String libelle, int montantPiecesOr, String devise,
                       Instant enregistreLe) {
}
