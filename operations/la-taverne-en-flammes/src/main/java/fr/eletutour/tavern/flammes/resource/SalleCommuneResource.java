package fr.eletutour.tavern.flammes.resource;

import fr.eletutour.tavern.flammes.arret.SalleCommuneService;
import fr.eletutour.tavern.flammes.arret.TourneeServie;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/taverne/salle")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "1. Arret gracieux", description = "Requetes longues a laisser finir avant de verrouiller les portes")
public class SalleCommuneResource {

    @Inject
    SalleCommuneService salleCommuneService;

    @GET
    @Path("/tournees")
    @Operation(
        summary = "Servir une tournee lente",
        description = "Bloque volontairement la requete. Envoyez un SIGTERM pendant le service pour verifier "
            + "que Quarkus attend la fin de la tournee dans la limite de quarkus.shutdown.timeout."
    )
    @APIResponse(
        responseCode = "200",
        description = "Tournee servie jusqu'au bout",
        content = @Content(schema = @Schema(implementation = TourneeServie.class))
    )
    public TourneeServie servirTournee(
        @QueryParam("aventurier") String aventurier,
        @QueryParam("secondes") @DefaultValue("5") int secondes
    ) {
        return salleCommuneService.servirTournee(aventurier, secondes);
    }

    @GET
    @Path("/tournees-en-cours")
    @Operation(summary = "Compter les tournees en cours", description = "Nombre de requetes encore en vol")
    public int tourneesEnCours() {
        return salleCommuneService.tourneesEnCours();
    }
}
