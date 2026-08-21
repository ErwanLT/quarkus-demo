package fr.eletutour.tavern.flammes.resource;

import fr.eletutour.tavern.flammes.grimoire.GrimoireDistant;
import fr.eletutour.tavern.flammes.grimoire.GrimoireService;
import fr.eletutour.tavern.flammes.grimoire.Recette;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/taverne/grimoire")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "3b. Timeout declaratif", description = "@Timeout, @Retry et @Fallback sur un appel sortant")
public class GrimoireResource {

    @Inject
    GrimoireService grimoireService;

    @Inject
    GrimoireDistant grimoireDistant;

    @GET
    @Path("/recettes")
    @Operation(
        summary = "Consulter une recette",
        description = "Protege par @Timeout(1500ms) + @Retry(1) + @Fallback. Reglez la latence ou la panne "
            + "du grimoire distant pour declencher la recette de secours."
    )
    @APIResponse(
        responseCode = "200",
        description = "Recette du grimoire ou recette de secours",
        content = @Content(schema = @Schema(implementation = Recette.class))
    )
    public Recette recette(@QueryParam("nom") String nom) {
        grimoireDistant.reinitialiserTentatives();
        return grimoireService.recupererRecette(nom);
    }

    @PUT
    @Path("/latences")
    @Operation(summary = "Regler la latence du grimoire distant", description = "En millisecondes")
    public long reglerLatence(@QueryParam("ms") @DefaultValue("50") long millisecondes) {
        grimoireDistant.reglerLatence(millisecondes);
        return grimoireDistant.latenceMs();
    }

    @PUT
    @Path("/pannes")
    @Operation(summary = "Mettre le grimoire distant en panne", description = "Les appels echouent immediatement")
    public boolean basculerPanne(@QueryParam("active") @DefaultValue("true") boolean active) {
        grimoireDistant.basculerPanne(active);
        return grimoireDistant.enPanne();
    }
}
