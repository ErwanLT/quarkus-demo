package fr.eletutour.tavern.security.jwt.jpa.controller;

import fr.eletutour.tavern.security.jwt.jpa.dto.LoginRequest;
import fr.eletutour.tavern.security.jwt.jpa.dto.LoginResponse;
import fr.eletutour.tavern.security.jwt.jpa.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/tavern/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Tavern Auth", description = "Authentification et generation de JWT.")
public class AuthResource {

    private final AuthService authService;

    @Inject
    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    @Operation(summary = "Login", description = "Genere un JWT pour acceder aux ressources protegees.")
    @APIResponse(
            responseCode = "200",
            description = "JWT genere",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
    )
    @APIResponse(responseCode = "401", description = "Identifiants invalides")
    public Response login(LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String token = authService.login(request.username(), request.password());
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        LoginResponse response = new LoginResponse(token, "Bearer", authService.expiresAtSeconds());
        return Response.ok(response).build();
    }
}
