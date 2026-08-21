package fr.eletutour.tavern.flammes.resource;

import fr.eletutour.tavern.flammes.corbeaux.EtatVoliere;
import fr.eletutour.tavern.flammes.corbeaux.VoliereService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/taverne/voliere")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "4. Vidange des corbeaux", description = "Constituer un retard de messages puis observer la vidange a l'arret")
public class VoliereResource {

    @Inject
    VoliereService voliereService;

    @GET
    @Operation(summary = "Etat de la voliere", description = "Corbeaux laches, traites et encore en vol")
    @APIResponse(
        responseCode = "200",
        description = "Retard courant",
        content = @Content(schema = @Schema(implementation = EtatVoliere.class))
    )
    public EtatVoliere etat() {
        return voliereService.etat();
    }

    @POST
    @Path("/lachers")
    @Operation(
        summary = "Lacher des corbeaux",
        description = "Alimente le canal corbeaux-en-vol. Le colombier les lit lentement, ce qui cree un retard visible."
    )
    public EtatVoliere lacher(@QueryParam("nombre") @DefaultValue("20") int nombre) {
        return voliereService.lacher(nombre);
    }

    @DELETE
    @Operation(summary = "Remettre les compteurs a zero", description = "Pour rejouer le scenario")
    public EtatVoliere reinitialiser() {
        return voliereService.reinitialiser();
    }
}
