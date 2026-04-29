package fr.eletutour.tavern.controller;

import fr.eletutour.tavern.client.TaverneClientApi;
import fr.eletutour.tavern.model.Aventurier;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/guilde")
public class GuildeResource {

    @Inject
    TaverneClientApi taverneClient;

    @GET
    @Path("/aventuriers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Aventurier> recupererAventuriers() {
        return taverneClient.aventuriers();
    }
}