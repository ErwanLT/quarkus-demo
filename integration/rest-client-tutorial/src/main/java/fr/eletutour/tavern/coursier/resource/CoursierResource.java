package fr.eletutour.tavern.coursier.resource;

import fr.eletutour.tavern.coursier.client.GrimoireClient;
import fr.eletutour.tavern.coursier.client.GuildeClient;
import fr.eletutour.tavern.coursier.model.Contrat;
import fr.eletutour.tavern.coursier.model.ContratAccepte;
import fr.eletutour.tavern.coursier.model.Difficulte;
import fr.eletutour.tavern.coursier.model.Recette;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

/**
 * Le coursier : la façade que la taverne expose à ses propres clients.
 *
 * <p>Elle ne fait aucun HTTP à la main : elle délègue au mandat
 * {@link GuildeClient} et au client construit à l'exécution {@link GrimoireClient}.</p>
 */
@Path("/coursier")
@Produces(MediaType.APPLICATION_JSON)
public class CoursierResource {

    @Inject
    @RestClient
    GuildeClient guilde;

    @Inject
    GrimoireClient grimoire;

    @GET
    @Path("/contrats")
    public List<Contrat> contrats(@QueryParam("difficulte") Difficulte difficulte) {
        return guilde.contrats(difficulte);
    }

    @GET
    @Path("/contrats/{id}")
    public Contrat contrat(@PathParam("id") long id) {
        return guilde.contrat(id);
    }

    @POST
    @Path("/contrats/{id}/acceptation")
    public ContratAccepte accepter(@PathParam("id") long id,
                                   @QueryParam("coursier") @DefaultValue("le coursier anonyme") String coursier) {
        return guilde.accepter(id, coursier);
    }

    @GET
    @Path("/grimoire/{nom}")
    public Recette recette(@PathParam("nom") String nom) {
        return grimoire.recette(nom);
    }
}
