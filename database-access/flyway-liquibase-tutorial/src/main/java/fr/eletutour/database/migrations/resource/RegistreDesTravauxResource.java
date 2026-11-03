package fr.eletutour.database.migrations.resource;

import fr.eletutour.database.migrations.registre.EtatRegistre;
import fr.eletutour.database.migrations.registre.RegistreFlywayService;
import fr.eletutour.database.migrations.registre.RegistreLiquibaseService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/taverne/registre")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Registre des travaux", description = "Comparer l'historique des migrations Flyway et Liquibase")
public class RegistreDesTravauxResource {

    @Inject
    RegistreFlywayService registreFlywayService;

    @Inject
    RegistreLiquibaseService registreLiquibaseService;

    @GET
    @Path("/flyway")
    @Operation(summary = "Registre Flyway", description = "Migrations appliquees et en attente cote Flyway")
    @APIResponse(
        responseCode = "200",
        description = "Etat du registre Flyway",
        content = @Content(schema = @Schema(implementation = EtatRegistre.class))
    )
    public EtatRegistre flyway() {
        return registreFlywayService.etat();
    }

    @GET
    @Path("/liquibase")
    @Operation(summary = "Registre Liquibase", description = "ChangeSets appliques et en attente cote Liquibase")
    @APIResponse(
        responseCode = "200",
        description = "Etat du registre Liquibase",
        content = @Content(schema = @Schema(implementation = EtatRegistre.class))
    )
    public EtatRegistre liquibase() {
        return registreLiquibaseService.etat();
    }
}
