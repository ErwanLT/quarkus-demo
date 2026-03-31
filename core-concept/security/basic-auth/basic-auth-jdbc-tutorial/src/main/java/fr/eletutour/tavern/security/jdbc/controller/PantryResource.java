package fr.eletutour.tavern.security.jdbc.controller;

import fr.eletutour.tavern.security.jdbc.dto.StockResponse;
import fr.eletutour.tavern.security.jdbc.service.PantryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/tavern")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Tavern Stock", description = "Acces a la reserve de nourriture et de biere.")
@SecurityScheme(securitySchemeName = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@SecurityRequirement(name = "basicAuth")
public class PantryResource {

    private final PantryService pantryService;

    @Inject
    public PantryResource(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GET
    @Path("/pantry")
    @RolesAllowed("keeper")
    @Operation(summary = "Reserve de nourriture", description = "Liste le contenu de la reserve.")
    @APIResponse(
            responseCode = "200",
            description = "Contenu de la reserve",
            content = @Content(schema = @Schema(implementation = StockResponse.class))
    )
    public StockResponse pantry() {
        return pantryService.pantry();
    }

    @GET
    @Path("/cellar")
    @RolesAllowed({"keeper", "supplier"})
    @Operation(summary = "Cave a biere", description = "Liste le contenu de la cave.")
    @APIResponse(
            responseCode = "200",
            description = "Contenu de la cave",
            content = @Content(schema = @Schema(implementation = StockResponse.class))
    )
    public StockResponse cellar() {
        return pantryService.cellar();
    }
}
