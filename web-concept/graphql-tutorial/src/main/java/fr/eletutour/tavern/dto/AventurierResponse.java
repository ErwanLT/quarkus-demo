package fr.eletutour.tavern.dto;

import fr.eletutour.tavern.model.Aventurier;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

/**
 * Représentation d'un aventurier pour l'API.
 */
@Description("Données publiques d'un aventurier")
public record AventurierResponse(@NonNull @Description("Identifiant unique") Long id,
    @NonNull @Description("Nom de l'aventurier") String nom,
    @NonNull @Description("Classe (ex: Guerrier, Mage)") String classe,
    @NonNull @Description("Niveau d'expérience") Integer niveau,
    @NonNull @Description("Liste des quêtes en cours ou terminées") List<@NonNull QueteResponse> quetes) {
    public static AventurierResponse fromDomain(Aventurier a) {
        if (a == null) return null;
        return new AventurierResponse(
            a.id,
            a.nom,
            a.classe,
            a.niveau,
            a.quetes != null ? a.quetes.stream().map(QueteResponse::fromDomain).toList() : List.of()
        );
    }
}
