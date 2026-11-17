package fr.eletutour.tavern.coursier.client;

import fr.eletutour.tavern.coursier.exception.business.CoursierException;
import fr.eletutour.tavern.coursier.model.Recette;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.io.Closeable;

/**
 * Accès au Grimoire. Cette interface n'est pas injectée directement :
 * elle est construite par {@link GrimoireClientProducer}, car son adresse
 * n'est connue qu'à l'exécution.
 *
 * <p>Elle étend {@link Closeable} : le client construit à la main doit être
 * fermé à la main, ce que fait le disposer du producer.</p>
 */
@Path("/grimoire/recettes")
public interface GrimoireClient extends Closeable {

    @GET
    @Path("/{nom}")
    Recette recette(@PathParam("nom") String nom);

    @ClientExceptionMapper
    static CoursierException versException(Response response) {
        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return CoursierException.recetteIntrouvable();
        }
        return CoursierException.reponseInattendue("Le Grimoire", response.getStatus());
    }
}
