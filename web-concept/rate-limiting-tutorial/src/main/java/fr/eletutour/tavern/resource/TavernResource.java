package fr.eletutour.tavern.resource;

import fr.eletutour.tavern.service.TavernService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/taverne")
public class TavernResource {

    private final TavernService tavernService;

    // Injection de dépendance via le constructeur (Bonne pratique recommandée, similaire à Spring)
    @Inject
    public TavernResource(TavernService tavernService) {
        this.tavernService = tavernService;
    }

    @POST
    @Path("/commande")
    @Produces(MediaType.TEXT_PLAIN)
    public Response orderBeer() {
        return Response.ok(tavernService.orderBeer()).build();
    }

    @GET
    @Path("/cave")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fetchFromCellar(@QueryParam("reset") boolean reset) {
        return Response.ok(tavernService.fetchFromCellar(reset)).build();
    }

    @GET
    @Path("/plat-du-jour")
    @Produces(MediaType.TEXT_PLAIN)
    public Response orderPlatDuJour(@QueryParam("empty") boolean empty) {
        return Response.ok(tavernService.orderPlatDuJour(empty)).build();
    }
}
