package fr.eletutour.tavern.controller;

import fr.eletutour.tavern.dto.TavernGreetingResponse;
import fr.eletutour.tavern.dto.TavernOrderRequest;
import fr.eletutour.tavern.dto.TavernOrderResponse;
import fr.eletutour.tavern.service.TavernService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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

@Path("/api/tavern")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TavernApiResource {

    private final TavernService tavernService;

    @Inject
    public TavernApiResource(TavernService tavernService) {
        this.tavernService = tavernService;
    }

    @GET
    @Path("/greeting")
    @Operation(summary = "Saluer un aventurier", description = "Retourne un message d'accueil de l'aubergiste.")
    @APIResponse(
            responseCode = "200",
            description = "Message d'accueil",
            content = @Content(schema = @Schema(implementation = TavernGreetingResponse.class))
    )
    public TavernGreetingResponse greeting(@QueryParam("name") String name) {
        return tavernService.greeting(name);
    }

    @POST
    @Path("/order")
    @Operation(summary = "Passer une commande", description = "Enregistre une commande a la taverne et renvoie un recu.")
    @APIResponse(
            responseCode = "200",
            description = "Recu de commande",
            content = @Content(schema = @Schema(implementation = TavernOrderResponse.class))
    )
    public TavernOrderResponse order(TavernOrderRequest request) {
        return tavernService.order(request);
    }
}
