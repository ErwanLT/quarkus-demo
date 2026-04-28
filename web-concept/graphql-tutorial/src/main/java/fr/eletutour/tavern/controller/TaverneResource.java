package fr.eletutour.tavern.controller;

import fr.eletutour.tavern.dto.AventurierInput;
import fr.eletutour.tavern.dto.AventurierResponse;
import fr.eletutour.tavern.service.TaverneService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

/**
 * Point d'entrée GraphQL pour la gestion de la Taverne.
 */
@GraphQLApi
@ApplicationScoped
public class TaverneResource {

    @Inject
    TaverneService taverneService;

    @Query("aventuriers")
    @Description("Liste tous les aventuriers présents dans la taverne.")
    public @NonNull List<@NonNull AventurierResponse> aventuriers() {
        return taverneService.getAllAventuriers().stream()
                .map(AventurierResponse::fromDomain)
                .toList();
    }

    @Query("aventurier")
    @Description("Recherche un aventurier par son identifiant unique.")
    public AventurierResponse aventurier(@Name("id") @NonNull Long id) {
        return AventurierResponse.fromDomain(taverneService.getAventurier(id));
    }

    @Mutation("ajouterAventurier")
    @Description("Ajoute un nouvel aventurier dans la taverne.")
    public @NonNull AventurierResponse ajouterAventurier(@Name("input") @NonNull AventurierInput input) {
        var aventurier = taverneService.ajouterAventurier(
                input.nom(),
                input.classe(),
                input.niveau()
        );
        return AventurierResponse.fromDomain(aventurier);
    }
}