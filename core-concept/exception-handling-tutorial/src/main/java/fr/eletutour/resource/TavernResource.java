package fr.eletutour.resource;

import fr.eletutour.model.Tavern;
import fr.eletutour.model.TavernCreateRequest;
import fr.eletutour.service.TavernService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/taverns")
@Produces(MediaType.APPLICATION_JSON)
public class TavernResource {

    @Inject
    TavernService service;

    @GET
    @Path("/{name}")
    public Tavern getTavern(@PathParam("name") String name) {
        return service.getTavern(name);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTavern(@Valid TavernCreateRequest request) {
        service.createTavern(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/{name}/enter")
    public Response enter(@PathParam("name") String name, @QueryParam("count") @DefaultValue("1") int count) {
        service.enterTavern(name, count);
        return Response.ok().build();
    }

    @GET
    @Path("/error")
    public Response triggerUnexpectedError() {
        throw new RuntimeException("Une erreur interne inattendue dans le système de gestion des tavernes");
    }
}
