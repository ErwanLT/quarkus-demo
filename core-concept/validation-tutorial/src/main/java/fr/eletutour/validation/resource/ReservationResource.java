package fr.eletutour.validation.resource;

import fr.eletutour.validation.model.Reservation;
import fr.eletutour.validation.model.ReservationRequest;
import fr.eletutour.validation.model.TypeChambre;
import fr.eletutour.validation.service.ReservationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/validation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    ReservationService service;

    @GET
    @Path("/chambres")
    public List<TypeChambre> chambres() {
        return List.of(TypeChambre.values());
    }

    @GET
    @Path("/reservations")
    public List<Reservation> reservations() {
        return service.lister();
    }

    @POST
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserver(@Valid ReservationRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(service.reserver(request))
                .build();
    }

    @GET
    @Path("/erreur")
    public Response erreur() {
        throw new IllegalStateException("La chaudière de la taverne a rendu l'âme");
    }
}
