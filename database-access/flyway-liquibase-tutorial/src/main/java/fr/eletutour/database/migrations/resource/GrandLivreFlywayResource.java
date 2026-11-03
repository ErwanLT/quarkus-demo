package fr.eletutour.database.migrations.resource;

import fr.eletutour.database.migrations.comptes.Ecriture;
import fr.eletutour.database.migrations.flyway.GrandLivreFlywayService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/taverne/flyway/grand-livre")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Flyway", description = "Grand Livre dont le schema est versionne par Flyway")
public class GrandLivreFlywayResource {

    @Inject
    GrandLivreFlywayService grandLivreFlywayService;

    @GET
    @Operation(summary = "Lire le grand livre migre par Flyway")
    public List<Ecriture> ecritures() {
        return grandLivreFlywayService.ecritures();
    }

    @POST
    @Path("/ecritures")
    @Operation(summary = "Facturer une tournee", description = "Ecrit la consommation et la taxe dans la table creee par Flyway")
    public Response facturer(
        @QueryParam("aventurier") @DefaultValue("Grimgor") String aventurier,
        @QueryParam("montant") @DefaultValue("30") int montantPiecesOr
    ) {
        return Response.status(Response.Status.CREATED)
            .entity(grandLivreFlywayService.facturer(aventurier, montantPiecesOr))
            .build();
    }

    @DELETE
    @Operation(summary = "Purger le grand livre Flyway")
    public long purger() {
        return grandLivreFlywayService.purger();
    }
}
