package fr.eletutour.tavern.dto;

import fr.eletutour.tavern.directive.Sensible;
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
                                 @Sensible @Description("Solde du coffre personnel de l'aventurier") Integer soldeOr) {
    public static AventurierResponse fromDomain(Aventurier a) {
        if (a == null) return null;
        return new AventurierResponse(
                a.id,
                a.nom,
                a.classe,
                a.niveau,
                a.soldeOr
        );
    }
}
