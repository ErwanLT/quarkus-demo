package fr.eletutour.fitnesse.resource;

import fr.eletutour.fitnesse.domain.Registre;
import fr.eletutour.fitnesse.domain.Reservation;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;

@Path("/taverne/reservations")
public class ReservationResource {

    @Inject
    Registre registre;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reserver(ReservationRequest demande, @Context UriInfo uriInfo) {
        Reservation reservation = registre.reserver(
                demande.nom(), demande.chambre(), demande.nuits() == null ? 1 : demande.nuits(), demande.membre());
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(reservation.numero())).build())
                .entity(ReservationResponse.depuis(reservation))
                .build();
    }

    @GET
    @Path("/{numero}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consulter(@PathParam("numero") int numero) {
        return registre.trouver(numero)
                .map(r -> Response.ok(ReservationResponse.depuis(r)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.TEXT_PLAIN + ";charset=UTF-8")
                        .entity("Aucune réservation n°" + numero + " dans le registre").build());
    }

    /** Remise à zéro du registre, appelée par la page SetUp de FitNesse. */
    @DELETE
    public Response effacerLArdoise() {
        registre.effacer();
        return Response.noContent().build();
    }

    public record ReservationRequest(String nom, String chambre, Integer nuits, boolean membre) {
    }

    public record ReservationResponse(int numero, String nom, String chambre, String libelle, int nuits,
                                      boolean membre, int prix, String message) {

        static ReservationResponse depuis(Reservation r) {
            return new ReservationResponse(r.numero(), r.nom(), r.chambre().name(), r.chambre().libelle(),
                    r.nuits(), r.membre(), r.prix(),
                    "Réservation n°" + r.numero() + " confirmée pour " + r.nom() + " : "
                            + r.chambre().libelle().toLowerCase() + ", " + r.nuits()
                            + (r.nuits() > 1 ? " nuits" : " nuit") + ", " + r.prix() + " pièces d'or");
        }
    }
}
