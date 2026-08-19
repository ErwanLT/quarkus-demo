package fr.eletutour.tavern.rest;

import fr.eletutour.tavern.raven.RavenMasterService;
import fr.eletutour.tavern.raven.RavenMessage;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;

/**
 * Point d'entree HTTP de la taverne pour depecher un corbeau messager.
 * <p>
 * Cette ressource reste volontairement fine : toute la logique de depart du
 * corbeau (publication Kafka) est deleguee a {@link RavenMasterService}.
 */
@Path("/ravens")
public class RavenResource {

    @Inject
    RavenMasterService ravenMasterService;

    /**
     * Depeche un corbeau messager charge du contenu fourni.
     *
     * @param request expediteur et contenu du message a transmettre
     * @return confirmation du depart du corbeau, avec l'instant de depart
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Depecher un corbeau messager",
            description = "Confie un message a un corbeau et le publie sur le topic Kafka raven-roost.")
    @APIResponse(
            responseCode = "202",
            description = "Le corbeau a quitte la taverne",
            content = @Content(schema = @Schema(implementation = RavenDispatchResponse.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Requete invalide (expediteur ou contenu manquant)"
    )
    public Response dispatch(@Valid @NotNull RavenDispatchRequest request) {
        RavenMessage message = ravenMasterService.dispatch(request.sender(), request.content());
        RavenDispatchResponse response =
                new RavenDispatchResponse(message.sender(), message.content(), message.sentAt());
        return Response.accepted(response).build();
    }
}
