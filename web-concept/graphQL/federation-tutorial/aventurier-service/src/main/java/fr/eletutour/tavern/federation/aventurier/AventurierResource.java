package fr.eletutour.tavern.federation.aventurier;

import fr.eletutour.tavern.federation.aventurier.model.Aventurier;
import fr.eletutour.tavern.federation.aventurier.service.AventurierService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

@GraphQLApi
@ApplicationScoped
public class AventurierResource {

    @Inject
    AventurierService aventurierService;

    @Query("aventuriers")
    @Description("Liste tous les aventuriers présents dans la taverne.")
    public @NonNull List<@NonNull Aventurier> aventuriers() {
        return aventurierService.getAllAventuriers();
    }

    /**
     * Sert de Query classique côté client, mais aussi de résolveur de
     * référence pour la fédération : le router l'appelle avec le seul id
     * quand un autre subgraph (quete-service) a besoin de l'entité complète.
     */
    @Query("aventurier")
    @Description("Recherche un aventurier par son identifiant unique.")
    public Aventurier aventurier(@Id @NonNull Long id) {
        return aventurierService.getAventurier(id);
    }
}
