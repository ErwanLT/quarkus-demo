package fr.eletutour.tavern.dto;

import fr.eletutour.tavern.model.Quete;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.NonNull;

/**
 * Représentation d'une quête pour l'API.
 */
@Description("Détails d'une quête")
public record QueteResponse(@NonNull @Description("Identifiant de la quête") Long id,
    @NonNull @Description("Intitulé") String titre,
    @Description("Niveau de difficulté") String difficulte,
    @Description("Montant de la prime") Integer recompenseOr) {
    public static QueteResponse fromDomain(Quete q) {
        if (q == null) return null;
        return new QueteResponse(q.id, q.titre, q.difficulte, q.recompenseOr);
    }
}
