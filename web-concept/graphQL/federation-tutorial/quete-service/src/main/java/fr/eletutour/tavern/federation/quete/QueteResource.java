package fr.eletutour.tavern.federation.quete;

import fr.eletutour.tavern.federation.quete.dto.QueteResponse;
import fr.eletutour.tavern.federation.quete.model.Aventurier;
import fr.eletutour.tavern.federation.quete.model.Quete;
import fr.eletutour.tavern.federation.quete.service.QueteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;
import java.util.Map;

@GraphQLApi
@ApplicationScoped
public class QueteResource {

    @Inject
    QueteService queteService;

    /**
     * Résolveur de référence : reconstruit le stub Aventurier étendu à
     * partir de sa seule clé. Le router l'appelle chaque fois qu'une requête
     * demande "quetes" sur un Aventurier obtenu depuis aventurier-service.
     */
    @Query
    public Aventurier aventurier(@Id @NonNull Long id) {
        return new Aventurier(id);
    }

    /**
     * Resolver batché : voir l'épisode 4 pour le même principe appliqué
     * en intra-service. Ici, SmallRye regroupe tous les Aventurier reçus
     * dans une même requête fédérée avant d'appeler cette méthode une seule
     * fois, à condition que federation.batch-resolving-enabled soit activé.
     */
    @Description("Liste des quêtes en cours ou terminées de l'aventurier")
    public List<List<QueteResponse>> quetes(@Source List<Aventurier> aventuriers) {
        System.out.println("Je passe ici");
        Map<Long, List<Quete>> quetesParAventurier = queteService.getQuetesForAventuriers(
                aventuriers.stream().map(Aventurier::getId).toList());

        return aventuriers.stream()
                .map(a -> quetesParAventurier.getOrDefault(a.getId(), List.of()).stream()
                        .map(QueteResponse::fromDomain)
                        .toList())
                .toList();
    }
}
