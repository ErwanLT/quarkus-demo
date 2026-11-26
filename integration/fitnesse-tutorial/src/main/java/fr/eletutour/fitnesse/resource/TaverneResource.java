package fr.eletutour.fitnesse.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/taverne")
public class TaverneResource {

    @GET
    @Path("/accueil")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public String accueil(@QueryParam("nom") String nom) {
        if (nom == null || nom.isBlank()) {
            return "Bienvenue à la taverne, voyageur !";
        }
        return "Bienvenue à la taverne, " + nom + " !";
    }

    @POST
    @Path("/reservation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public Response reserver(ReservationRequest demande) {
        if (demande.nom() == null || demande.nom().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Le nom est obligatoire").build();
        }
        if (demande.chambre() == null || demande.chambre().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La chambre est obligatoire").build();
        }
        return Response.status(Response.Status.CREATED)
                .entity("Réservation confirmée pour " + demande.nom() + " en " + demande.chambre())
                .build();
    }

    public record ReservationRequest(String nom, String chambre) {
    }
}
