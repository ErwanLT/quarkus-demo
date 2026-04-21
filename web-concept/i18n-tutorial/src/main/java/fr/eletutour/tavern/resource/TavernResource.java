package fr.eletutour.tavern.resource;

import fr.eletutour.tavern.service.TavernService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Contrôleur REST de la taverne.
 * <p>
 * Chaque endpoint injecte les {@link HttpHeaders} via {@code @Context}
 * et les transmet au service pour résoudre la locale du client depuis
 * l'en-tête {@code Accept-Language}.
 * </p>
 *
 * <p>Exemple d'appel :</p>
 * <pre>
 *   curl -H "Accept-Language: en-US" http://localhost:8080/taverne/commande
 *   curl -H "Accept-Language: de-DE" http://localhost:8080/taverne/accueil?nom=Thorin
 * </pre>
 */
@Path("/taverne")
@Produces(MediaType.TEXT_PLAIN)
public class TavernResource {

    private final TavernService tavernService;

    @Inject
    public TavernResource(TavernService tavernService) {
        this.tavernService = tavernService;
    }

    // ---------------------------------------------------------------
    // Comptoir
    // ---------------------------------------------------------------

    /**
     * Commande une bière.
     * Protégé par Rate Limiting — 3 appels / 10 secondes.
     *
     * @param headers injectés par JAX-RS, contiennent {@code Accept-Language}
     */
    @POST
    @Path("/commande")
    public Response orderBeer(@Context HttpHeaders headers) {
        return Response.ok(tavernService.orderBeer(headers)).build();
    }

    // ---------------------------------------------------------------
    // Cave
    // ---------------------------------------------------------------

    /**
     * Demande au tavernier de descendre à la cave.
     * Équipé d'un Retry (max 3 tentatives, délai 200ms).
     *
     * @param reset   si {@code true}, réinitialise le compteur avant de descendre
     * @param headers injectés par JAX-RS
     */
    @GET
    @Path("/cave")
    public Response fetchFromCellar(
            @QueryParam("reset") @DefaultValue("false") boolean reset,
            @Context HttpHeaders headers) {

        if (reset) {
            return Response.ok(tavernService.resetCellar(headers)).build();
        }
        return Response.ok(tavernService.fetchFromCellar(headers)).build();
    }

    // ---------------------------------------------------------------
    // Cuisine
    // ---------------------------------------------------------------

    /**
     * Commande le plat du jour.
     * Équipé d'un Fallback si la marmite est vide.
     *
     * @param empty   si {@code true}, simule une rupture de stock
     * @param headers injectés par JAX-RS
     */
    @GET
    @Path("/plat-du-jour")
    public Response orderPlatDuJour(
            @QueryParam("empty") @DefaultValue("false") boolean empty,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.orderPlatDuJour(empty, headers)).build();
    }

    // ---------------------------------------------------------------
    // Accueil & informations (démo l10n avancée)
    // ---------------------------------------------------------------

    /**
     * Accueille un aventurier par son nom dans sa langue.
     *
     * @param nom     le nom ou titre de l'aventurier
     * @param headers injectés par JAX-RS
     */
    @GET
    @Path("/accueil")
    public Response accueillir(
            @QueryParam("nom") @DefaultValue("aventurier") String nom,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.accueillir(nom, headers)).build();
    }

    /**
     * Annonce le prix d'un article avec devise localisée.
     * Démontre la l10n monétaire : même montant, format différent selon la locale.
     *
     * @param article le nom de l'article
     * @param montant le prix en valeur numérique
     * @param headers injectés par JAX-RS
     */
    @GET
    @Path("/prix")
    public Response annoncerPrix(
            @QueryParam("article") @DefaultValue("bière") String article,
            @QueryParam("montant") @DefaultValue("3.50") double montant,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.annoncerPrix(article, montant, headers)).build();
    }

    /**
     * Annonce l'affluence courante avec pluriel localisé.
     * Démontre la l10n des pluriels : règles différentes selon la langue.
     *
     * @param nombre  le nombre d'aventuriers présents
     * @param headers injectés par JAX-RS
     */
    @GET
    @Path("/affluence")
    public Response annoncerAffluence(
            @QueryParam("nombre") @DefaultValue("1") int nombre,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.annoncerAffluence(nombre, headers)).build();
    }
}
