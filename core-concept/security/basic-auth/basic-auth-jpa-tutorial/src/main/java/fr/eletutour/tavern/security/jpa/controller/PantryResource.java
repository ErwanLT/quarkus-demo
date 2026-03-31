package fr.eletutour.tavern.security.jpa.controller;

import fr.eletutour.tavern.security.jpa.dto.StockResponse;
import fr.eletutour.tavern.security.jpa.service.PantryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/tavern")
@Produces(MediaType.APPLICATION_JSON)
public class PantryResource {

    private final PantryService pantryService;

    @Inject
    public PantryResource(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GET
    @Path("/pantry")
    @RolesAllowed("keeper")
    public StockResponse pantry() {
        return pantryService.pantry();
    }

    @GET
    @Path("/cellar")
    @RolesAllowed({"keeper", "supplier"})
    public StockResponse cellar() {
        return pantryService.cellar();
    }
}
