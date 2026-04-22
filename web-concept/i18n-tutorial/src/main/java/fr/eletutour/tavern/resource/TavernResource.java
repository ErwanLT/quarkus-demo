package fr.eletutour.tavern.resource;

import fr.eletutour.tavern.locale.LocaleHelper;
import fr.eletutour.tavern.service.TavernService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Contrôleur REST de la taverne.
 * <p>
 * Chaque endpoint injecte les {@link HttpHeaders} via {@code @Context},
 * résout la locale du client via {@link LocaleHelper} puis délègue
 * exclusivement le cas d'usage métier au service.
 * </p>
 *
 * <p>Exemple d'appel :</p>
 * <pre>
 *   curl -H "Accept-Language: en-US" -X POST http://localhost:8080/taverne/commandes
 *   curl -H "Accept-Language: de-DE" http://localhost:8080/taverne/salutations?nom=Thorin
 * </pre>
 */
@Path("/taverne")
@Produces(MediaType.TEXT_PLAIN)
@Tag(name = "Taverne i18n", description = "Endpoints de démonstration i18n/l10n avec Fault Tolerance")
public class TavernResource {

    private final TavernService tavernService;
    private final LocaleHelper localeHelper;

    @Inject
    public TavernResource(TavernService tavernService, LocaleHelper localeHelper) {
        this.tavernService = tavernService;
        this.localeHelper = localeHelper;
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
    @Path("/commandes")
    @Operation(
            summary = "Commander une bière",
            description = "Retourne un message localisé selon Accept-Language. Endpoint protégé par rate limiting."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Bière servie avec message localisé",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
            ),
            @APIResponse(
                    responseCode = "429",
                    description = "Trop de commandes dans la fenêtre de temps",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
            )
    })
    public Response orderBeer(@Context HttpHeaders headers) {
        return Response.ok(tavernService.orderBeer(localeHelper.resolveLocale(headers))).build();
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
    @Path("/approvisionnements-cave")
    @Operation(
            summary = "Descendre à la cave",
            description = "Simule une opération instable avec retry automatique."
    )
    @APIResponse(
            responseCode = "200",
            description = "Résultat localisé de l'opération cave",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
    )
    public Response fetchFromCellar(
            @Parameter(description = "Réinitialise le compteur des tentatives cave", example = "false")
            @QueryParam("reset") @DefaultValue("false") boolean reset,
            @Context HttpHeaders headers) {
        var locale = localeHelper.resolveLocale(headers);

        if (reset) {
            return Response.ok(tavernService.resetCellar(locale)).build();
        }
        return Response.ok(tavernService.fetchFromCellar(locale)).build();
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
    @Path("/plats-du-jour")
    @Operation(
            summary = "Commander le plat du jour",
            description = "Retourne le plat principal ou le fallback en cas de rupture."
    )
    @APIResponse(
            responseCode = "200",
            description = "Réponse localisée (plat principal ou fallback)",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
    )
    public Response orderPlatDuJour(
            @Parameter(description = "Simule une marmite vide pour déclencher le fallback", example = "false")
            @QueryParam("empty") @DefaultValue("false") boolean empty,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.orderPlatDuJour(empty, localeHelper.resolveLocale(headers))).build();
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
    @Path("/salutations")
    @Operation(
            summary = "Message d'accueil localisé",
            description = "Retourne un message de bienvenue dans la langue demandée."
    )
    @APIResponse(
            responseCode = "200",
            description = "Message d'accueil localisé",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
    )
    public Response accueillir(
            @Parameter(description = "Nom ou titre de l'aventurier", example = "Thorin")
            @QueryParam("nom") @DefaultValue("aventurier") String nom,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.accueillir(nom, localeHelper.resolveLocale(headers))).build();
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
    @Path("/tarifs")
    @Operation(
            summary = "Annonce d'un prix localisé",
            description = "Formate le montant selon la locale (devise, séparateurs et règle spécifique latin)."
    )
    @APIResponse(
            responseCode = "200",
            description = "Prix localisé",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
    )
    public Response annoncerPrix(
            @Parameter(description = "Article concerné", example = "bière")
            @QueryParam("article") @DefaultValue("bière") String article,
            @Parameter(description = "Montant numérique à formatter selon la locale", example = "3.50")
            @QueryParam("montant") @DefaultValue("3.50") double montant,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.annoncerPrix(article, montant, localeHelper.resolveLocale(headers))).build();
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
    @Operation(
            summary = "Annonce de l'affluence",
            description = "Retourne une phrase localisée avec forme singulier/pluriel adaptée."
    )
    @APIResponse(
            responseCode = "200",
            description = "Affluence localisée",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
    )
    public Response annoncerAffluence(
            @Parameter(description = "Nombre d'aventuriers présents", example = "2")
            @QueryParam("nombre") @DefaultValue("1") int nombre,
            @Context HttpHeaders headers) {

        return Response.ok(tavernService.annoncerAffluence(nombre, localeHelper.resolveLocale(headers))).build();
    }
}
