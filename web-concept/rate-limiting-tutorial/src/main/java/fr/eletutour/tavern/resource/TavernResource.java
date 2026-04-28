package fr.eletutour.tavern.resource;

import fr.eletutour.tavern.service.TavernService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Point d'entrée REST (Contrôleur) pour la taverne.
 * <p>
 * Expose les différentes actions possibles pour un aventurier souhaitant
 * commander à boire ou à manger, et gère la traduction HTTP des réponses.
 * </p>
 */
@Path("/taverne")
public class TavernResource {

    private final TavernService tavernService;

    /**
     * Constructeur avec injection de dépendance.
     * <p>
     * Privilégie l'injection par constructeur pour garantir l'immutabilité 
     * et faciliter les tests unitaires isolés.
     * </p>
     *
     * @param tavernService le service métier encapsulant la logique de la Taverne
     */
    @Inject
    public TavernResource(TavernService tavernService) {
        this.tavernService = tavernService;
    }

    /**
     * Permet à un aventurier de commander une bière.
     * <p>
     * Ce point de contact est protégé par une limitation de fréquence (Rate Limiting).
     * </p>
     *
     * @return une {@link Response} avec HTTP 200 et la confirmation de la commande,
     *         ou HTTP 429 si le tavernier est débordé (géré via exception)
     */
    @POST
    @Path("/commande")
    @Produces(MediaType.TEXT_PLAIN)
    public Response orderBeer() {
        return Response.ok(tavernService.orderBeer()).build();
    }

    /**
     * Permet à un aventurier de demander une bouteille de la cave.
     * <p>
     * Ce point de contact expose la résilience (Retry) côté service.
     * </p>
     *
     * @return une {@link Response} avec HTTP 200 contenant la bouteille remontée
     */
    @GET
    @Path("/cave")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fetchFromCellar(@QueryParam("reset") boolean reset) {
        if (reset) {
            tavernService.resetCellarTrips();
        }
        return Response.ok(tavernService.fetchFromCellar()).build();
    }

    /**
     * Permet à un aventurier de commander le plat du jour.
     * <p>
     * Si le plat est en rupture, retourne quand même HTTP 200 avec le plat de secours.
     * </p>
     *
     * @param empty permet de simuler une rupture de stock du plat du jour
     * @return une {@link Response} avec HTTP 200 contenant le repas (principal ou secours)
     */
    @GET
    @Path("/plat-du-jour")
    @Produces(MediaType.TEXT_PLAIN)
    public Response orderPlatDuJour(@QueryParam("empty") boolean empty) {
        return Response.ok(tavernService.orderPlatDuJour(empty)).build();
    }
}
