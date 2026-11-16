package fr.eletutour.tavern.coursier.client;

import fr.eletutour.tavern.coursier.exception.ContratIntrouvableException;
import fr.eletutour.tavern.coursier.model.Contrat;
import fr.eletutour.tavern.coursier.model.ContratAccepte;
import fr.eletutour.tavern.coursier.model.Difficulte;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Mandat du coursier auprès de la Guilde.
 *
 * <p>L'interface décrit les gestes ; Quarkus génère l'implémentation HTTP.
 * Le {@code configKey} fait pointer la configuration sur le préfixe
 * {@code quarkus.rest-client.guilde}.</p>
 */
@Path("/guilde/contrats")
@RegisterRestClient(configKey = "guilde")
@ClientHeaderParam(name = "X-Taverne", value = "The Falling Whale")
public interface GuildeClient {

    @GET
    List<Contrat> contrats(@QueryParam("difficulte") Difficulte difficulte);

    @GET
    @Path("/{id}")
    Contrat contrat(@PathParam("id") long id);

    @POST
    @Path("/{id}/acceptation")
    ContratAccepte accepter(@PathParam("id") long id, @HeaderParam("X-Coursier") String coursier);

    /**
     * Traduit le 404 HTTP de la Guilde en exception métier, avant même que
     * le resource du coursier ne voie la réponse.
     */
    @ClientExceptionMapper
    static ContratIntrouvableException versException(Response response) {
        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return new ContratIntrouvableException(
                    "La Guilde ne connaît pas ce contrat : il a peut-être déjà été retiré du tableau.");
        }
        return null;
    }
}
