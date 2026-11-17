package fr.eletutour.tavern.coursier.guilde;

import fr.eletutour.tavern.coursier.exception.model.Probleme;
import fr.eletutour.tavern.coursier.model.Recette;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Grimoire des recettes simulé, consulté par le client programmatique.
 */
@Path("/grimoire/recettes")
@Produces(MediaType.APPLICATION_JSON)
public class GrimoireSimule {

    private static final Map<String, Recette> RECETTES = Map.of(
            "hydromel", new Recette("Hydromel du barde",
                    List.of("miel", "eau", "levure"), 21),
            "ragout-de-sanglier", new Recette("Ragoût de sanglier aux herbes",
                    List.of("sanglier", "orge", "carottes", "thym de druide"), 0),
            "elixir-de-lune", new Recette("Élixir de lune",
                    List.of("mandragore", "rosée", "plume de harpie"), 3));

    @GET
    @Path("/{nom}")
    public Recette recette(@PathParam("nom") String nom) {
        Recette recette = RECETTES.get(nom.toLowerCase(Locale.ROOT));
        if (recette == null) {
            throw new WebApplicationException(Probleme.reponse(404, "Recette introuvable",
                    "Le Grimoire ne contient aucune recette nommée " + nom + "."));
        }
        return recette;
    }
}
