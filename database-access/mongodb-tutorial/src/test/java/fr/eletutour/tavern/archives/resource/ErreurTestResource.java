package fr.eletutour.tavern.archives.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/** Déclencheur d'erreur inattendue, présent uniquement dans les tests. */
@Path("/archives/erreur-test")
public class ErreurTestResource {

    @GET
    @Path("/boom")
    public String boom() {
        throw new IllegalStateException("Message technique qui ne doit pas fuir");
    }
}
