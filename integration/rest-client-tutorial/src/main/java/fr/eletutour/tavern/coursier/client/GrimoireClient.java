package fr.eletutour.tavern.coursier.client;

import fr.eletutour.tavern.coursier.exception.RecetteIntrouvableException;
import fr.eletutour.tavern.coursier.model.Recette;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * Accès au Grimoire. Cette interface n'est pas injectée directement :
 * elle est construite par {@link GrimoireClientProducer}, car son adresse
 * n'est connue qu'à l'exécution.
 */
@Path("/grimoire/recettes")
public interface GrimoireClient {

    @GET
    @Path("/{nom}")
    Recette recette(@PathParam("nom") String nom);

    @ClientExceptionMapper
    static RecetteIntrouvableException versException(Response response) {
        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return new RecetteIntrouvableException(
                    "Le Grimoire est muet sur cette recette.");
        }
        return null;
    }
}
