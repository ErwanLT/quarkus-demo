package fr.eletutour.tavern.federation.quete.dto;

import fr.eletutour.tavern.federation.quete.model.Quete;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.NonNull;

@Description("Détails d'une quête")
public record QueteResponse(@NonNull Long id,
    @NonNull String titre,
    String difficulte,
    Integer recompenseOr) {
    public static QueteResponse fromDomain(Quete q) {
        if (q == null) return null;
        return new QueteResponse(q.id, q.titre, q.difficulte, q.recompenseOr);
    }
}
