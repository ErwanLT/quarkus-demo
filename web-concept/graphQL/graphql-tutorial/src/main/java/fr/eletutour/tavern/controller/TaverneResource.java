package fr.eletutour.tavern.controller;

import fr.eletutour.tavern.dto.AventurierConnection;
import fr.eletutour.tavern.dto.AventurierInput;
import fr.eletutour.tavern.dto.AventurierResponse;
import fr.eletutour.tavern.dto.QueteResponse;
import fr.eletutour.tavern.model.Quete;
import fr.eletutour.tavern.service.TaverneService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.graphql.*;

import java.util.List;
import java.util.Map;

/**
 * Point d'entrée GraphQL pour la gestion de la Taverne.
 */
@GraphQLApi
@ApplicationScoped
public class TaverneResource {

    @Inject
    TaverneService taverneService;

    @Inject
    @ConfigProperty(name = "tavern.pagination.default-limit", defaultValue = "10")
    int defaultLimit;

    @Inject
    @ConfigProperty(name = "tavern.pagination.default-first", defaultValue = "10")
    int defaultFirst;

    @Deprecated
    @Query("aventuriers")
    @Description("Préférer aventuriersConnection, pour un parcours plus stable du registre.")
    public @NonNull List<@NonNull AventurierResponse> aventuriers(
            @Description("Index du premier aventurier à servir") Integer offset,
            @Description("Nombre maximum d'aventuriers à servir") Integer limit) {
        int effectiveOffset = offset != null ? offset : 0;
        int effectiveLimit = limit != null ? limit : defaultLimit;
        return taverneService.getAventuriers(effectiveOffset, effectiveLimit).stream()
                .map(AventurierResponse::fromDomain)
                .toList();
    }

    @Query("aventuriersConnection")
    @Description("Parcourt le registre par curseur, page après page.")
    public AventurierConnection aventuriersConnection(
            @Description("Curseur après lequel reprendre la lecture") String after,
            @Description("Nombre d'aventuriers à servir") Integer first) {
        return taverneService.getAventuriersConnection(after, first != null ? first : defaultFirst);
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

    /**
     * Résolveur batché : SmallRye GraphQL regroupe tous les aventuriers de la requête
     * en cours et n'appelle cette méthode qu'une seule fois, plutôt qu'une fois par
     * aventurier. La liste renvoyée doit garder la même taille et le même ordre que
     * la liste reçue en paramètre.
     */
    @Description("Liste des quêtes en cours ou terminées de l'aventurier")
    public List<List<QueteResponse>> quetes(@Source List<AventurierResponse> aventuriers) {
        Map<Long, List<Quete>> quetesParAventurier = taverneService.getQuetesForAventuriers(
                aventuriers.stream().map(AventurierResponse::id).toList());

        return aventuriers.stream()
                .map(a -> quetesParAventurier.getOrDefault(a.id(), List.of()).stream()
                        .map(QueteResponse::fromDomain)
                        .toList())
                .toList();
    }
}