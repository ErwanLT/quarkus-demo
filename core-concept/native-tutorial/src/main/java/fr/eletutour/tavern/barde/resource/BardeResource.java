package fr.eletutour.tavern.barde.resource;

import fr.eletutour.tavern.barde.instrument.Luthier;
import fr.eletutour.tavern.barde.repertoire.Ballade;
import fr.eletutour.tavern.barde.repertoire.Recueil;
import fr.eletutour.tavern.barde.statue.Gardien;
import fr.eletutour.tavern.barde.statue.Statue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/barde")
@Produces(MediaType.APPLICATION_JSON)
public class BardeResource {

    private final Luthier luthier;
    private final Recueil recueil;
    private final Gardien gardien;

    public BardeResource(Luthier luthier, Recueil recueil, Gardien gardien) {
        this.luthier = luthier;
        this.recueil = recueil;
        this.gardien = gardien;
    }

    /** Écueil n°1 : la réflexion ({@code Class.forName}). */
    @GET
    @Path("/instruments/{nom}")
    public Melodie jouer(@PathParam("nom") String nom) {
        return new Melodie(nom, luthier.fabriquer(nom).jouer());
    }

    /** Écueil n°1 bis + n°2 : un fichier JSON désérialisé par Jackson dans un type invisible. */
    @GET
    @Path("/repertoire")
    public List<String> repertoire() {
        return recueil.repertoire().ballades();
    }

    /** Écueil n°2 : les ressources lues avec {@code getResourceAsStream}. */
    @GET
    @Path("/ballades/{titre}")
    public Ballade ballade(@PathParam("titre") String titre) {
        return recueil.ballade(titre);
    }

    /** Écueil n°3 : l'initialisation au build ou au run. */
    @GET
    @Path("/statue")
    public Statue statue() {
        return gardien.observer();
    }

    public record Melodie(String instrument, String son) {
    }
}
