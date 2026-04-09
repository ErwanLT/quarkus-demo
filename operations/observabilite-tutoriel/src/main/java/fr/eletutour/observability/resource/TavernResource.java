package fr.eletutour.observability.resource;

import fr.eletutour.observability.model.OrderResponse;
import fr.eletutour.observability.service.TavernService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/tavern")
@Produces(MediaType.APPLICATION_JSON)
public class TavernResource {

    @Inject
    TavernService tavernService;

    @GET
    @Path("/order")
    public OrderResponse order(@QueryParam("drink") String drink) {
        return tavernService.placeOrder(drink);
    }
}
