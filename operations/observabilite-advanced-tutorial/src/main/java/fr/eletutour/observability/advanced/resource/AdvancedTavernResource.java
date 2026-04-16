package fr.eletutour.observability.advanced.resource;

import fr.eletutour.observability.advanced.model.OrderResponse;
import fr.eletutour.observability.advanced.service.AdvancedTavernService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/advanced-tavern")
@Produces(MediaType.APPLICATION_JSON)
public class AdvancedTavernResource {

    @Inject
    AdvancedTavernService tavernService;

    @GET
    @Path("/order")
    @Operation(
        summary = "Place an advanced tavern order",
        description = "Processes a drink order and returns the serving result."
    )
    @APIResponse(
        responseCode = "200",
        description = "Order processed",
        content = @Content(schema = @Schema(implementation = OrderResponse.class))
    )
    public OrderResponse order(@Parameter(description = "Drink to order", example = "stout") @QueryParam("drink") String drink) {
        return tavernService.placeOrder(drink);
    }
}
