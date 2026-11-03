package fr.eletutour.database.migrations.resource;

import fr.eletutour.database.migrations.comptes.Ecriture;
import fr.eletutour.database.migrations.liquibase.GrandLivreLiquibaseService;
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

@Path("/taverne/liquibase/grand-livre")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Liquibase", description = "Grand Livre dont le schema est versionne par Liquibase")
public class GrandLivreLiquibaseResource {

    @Inject
    GrandLivreLiquibaseService grandLivreLiquibaseService;

    @GET
    @Operation(summary = "Lire le grand livre migre par Liquibase")
    public List<Ecriture> ecritures() {
        return grandLivreLiquibaseService.ecritures();
    }

    @POST
    @Path("/ecritures")
    @Operation(summary = "Facturer une tournee", description = "Ecrit la consommation et la taxe dans la table creee par Liquibase")
    public Response facturer(
        @QueryParam("aventurier") @DefaultValue("Grimgor") String aventurier,
        @QueryParam("montant") @DefaultValue("30") int montantPiecesOr
    ) {
        return Response.status(Response.Status.CREATED)
            .entity(grandLivreLiquibaseService.facturer(aventurier, montantPiecesOr))
            .build();
    }

    @DELETE
    @Operation(summary = "Purger le grand livre Liquibase")
    public long purger() {
        return grandLivreLiquibaseService.purger();
    }
}
