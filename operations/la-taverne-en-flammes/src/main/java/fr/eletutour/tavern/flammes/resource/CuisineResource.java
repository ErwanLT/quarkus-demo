package fr.eletutour.tavern.flammes.resource;

import fr.eletutour.tavern.flammes.cuisine.CuisineService;
import fr.eletutour.tavern.flammes.cuisine.Repas;
import io.smallrye.mutiny.Uni;
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

@Path("/taverne/cuisine")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "3a. Timeout reactif", description = "Borner l'attente d'une operation asynchrone avec Mutiny")
public class CuisineResource {

    @Inject
    CuisineService cuisineService;

    @GET
    @Path("/commandes")
    @Operation(
        summary = "Commander un plat",
        description = "Au-dela de taverne.cuisine.timeout, la chaine Mutiny echoue puis se rattrape "
            + "sur un repas de secours. Jouez sur preparationMs pour passer d'un cas a l'autre."
    )
    @APIResponse(
        responseCode = "200",
        description = "Plat servi ou repas de secours",
        content = @Content(schema = @Schema(implementation = Repas.class))
    )
    public Uni<Repas> commander(
        @QueryParam("plat") String plat,
        @QueryParam("preparationMs") @DefaultValue("100") long preparationMs
    ) {
        return cuisineService.commanderRepas(plat, preparationMs);
    }

    @GET
    @Path("/timeout")
    @Operation(summary = "Lire le timeout configure", description = "Valeur de taverne.cuisine.timeout en ms")
    public long timeout() {
        return cuisineService.timeoutCuisine().toMillis();
    }
}
