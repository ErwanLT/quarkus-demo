package fr.eletutour.fitnesse.resource;

import fr.eletutour.fitnesse.domain.ChambreComplete;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Traduit les refus métier en statuts HTTP lisibles : 400 pour une demande
 * invalide, 409 quand la chambre est déjà complète.
 */
public class ErreursTaverne {

    @ServerExceptionMapper
    public Response demandeInvalide(IllegalArgumentException e) {
        return texte(Response.Status.BAD_REQUEST, e.getMessage());
    }

    @ServerExceptionMapper
    public Response chambreComplete(ChambreComplete e) {
        return texte(Response.Status.CONFLICT, e.getMessage());
    }

    private Response texte(Response.Status statut, String message) {
        return Response.status(statut).type(MediaType.TEXT_PLAIN + ";charset=UTF-8").entity(message).build();
    }
}
