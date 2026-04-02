package fr.eletutour.tavern.security.jwt.jpa.controller;

import fr.eletutour.tavern.security.jwt.jpa.dto.LedgerResponse;
import fr.eletutour.tavern.security.jwt.jpa.dto.VaultResponse;
import fr.eletutour.tavern.security.jwt.jpa.service.TreasuryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/tavern")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Tavern Treasury", description = "Acces au livre de comptes et au coffre fort.")
@SecurityScheme(securitySchemeName = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "jwt")
public class TreasuryResource {

    private final TreasuryService treasuryService;

    @Inject
    public TreasuryResource(TreasuryService treasuryService) {
        this.treasuryService = treasuryService;
    }

    @GET
    @Path("/ledger")
    @RolesAllowed("accountant")
    @Operation(summary = "Livre de comptes", description = "Expose les entrees du livre de comptes.")
    @APIResponse(
            responseCode = "200",
            description = "Entrees du livre de comptes",
            content = @Content(schema = @Schema(implementation = LedgerResponse.class))
    )
    public LedgerResponse ledger() {
        return treasuryService.ledger();
    }

    @GET
    @Path("/vault")
    @RolesAllowed("treasurer")
    @Operation(summary = "Coffre fort", description = "Liste les biens contenus dans le coffre.")
    @APIResponse(
            responseCode = "200",
            description = "Contenu du coffre",
            content = @Content(schema = @Schema(implementation = VaultResponse.class))
    )
    public VaultResponse vault() {
        return treasuryService.vault();
    }
}
