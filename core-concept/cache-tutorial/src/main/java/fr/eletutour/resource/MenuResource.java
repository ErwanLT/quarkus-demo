package fr.eletutour.resource;

import fr.eletutour.model.Plat;
import fr.eletutour.service.MenuService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * Comptoir de la taverne : point d'entrée des aventuriers pour consulter
 * et gérer le menu du jour.
 *
 * <p>Ce resource démontre trois interactions avec l'ardoise magique :</p>
 * <ol>
 *   <li><b>GET /menu/{jour}</b> : consulter l'ardoise (cache hit/miss)</li>
 *   <li><b>DELETE /menu/{jour}</b> : effacer un jour de l'ardoise</li>
 *   <li><b>DELETE /menu</b> : effacer toute l'ardoise</li>
 * </ol>
 */
@Path("/menu")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Ardoise Magique", description = "Gestion du menu du jour via le cache Quarkus")
public class MenuResource {

    @Inject
    MenuService menuService;

    /**
     * Retourne le menu du jour pour un jour donné.
     *
     * <p>Appel 1 : cache miss → descente à la cave (200 ms)</p>
     * <p>Appel 2+ : cache hit → réponse immédiate depuis l'ardoise</p>
     *
     * @param jour Le jour de la semaine (lundi, mardi, mercredi, jeudi, vendredi)
     * @return La liste des plats du menu
     */
    @GET
    @Path("/{jour}")
    @Operation(
            summary = "Consulter l'ardoise",
            description = "Retourne le menu du jour. Au premier appel, le tavernier descend " +
                          "à la cave (200 ms). Aux suivants, il lève simplement les yeux vers " +
                          "l'ardoise magique (< 1 ms)."
    )
    @APIResponse(responseCode = "200", description = "Menu du jour retourné avec succès")
    @APIResponse(responseCode = "404", description = "Jour inconnu au grand livre")
    public Response consulterArdoise(
            @PathParam("jour")
            @Parameter(description = "Jour de la semaine", example = "lundi")
            String jour) {

        List<Plat> menu = menuService.obtenirMenuDuJour(jour);
        if (menu.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Le grand livre ne connaît pas le menu du jour : " + jour)
                    .build();
        }
        return Response.ok(menu).build();
    }

    /**
     * Efface le menu d'un jour spécifique de l'ardoise.
     *
     * <p>Utile quand le chef change le menu en cours de journée.
     * Le prochain appel GET pour ce jour refera une descente à la cave.</p>
     *
     * @param jour Le jour à effacer de l'ardoise
     * @return 204 No Content
     */
    @DELETE
    @Path("/{jour}")
    @Operation(
            summary = "Effacer un jour de l'ardoise",
            description = "Invalide le cache pour un jour donné. Le prochain GET " +
                          "redescendra à la cave pour ce jour-là."
    )
    @APIResponse(responseCode = "204", description = "Entrée effacée de l'ardoise")
    public Response effacerJour(
            @PathParam("jour")
            @Parameter(description = "Jour à effacer", example = "lundi")
            String jour) {

        menuService.effacerMenuDuJour(jour);
        return Response.noContent().build();
    }

    /**
     * Efface entièrement l'ardoise (tous les jours).
     *
     * <p>Typiquement utilisé en début de semaine pour forcer la relecture
     * complète du grand livre à la cave.</p>
     *
     * @return 204 No Content
     */
    @DELETE
    @Operation(
            summary = "Effacer toute l'ardoise",
            description = "Invalide l'intégralité du cache. Les prochains appels GET " +
                          "pour chaque jour redescendront à la cave."
    )
    @APIResponse(responseCode = "204", description = "Ardoise entièrement effacée")
    public Response effacerTouteArdoise() {
        menuService.effacerTouteArdoise();
        return Response.noContent().build();
    }
}
