package fr.eletutour.fitnesse.resource;

import fr.eletutour.fitnesse.domain.Carte;
import fr.eletutour.fitnesse.domain.Chambre;
import fr.eletutour.fitnesse.domain.Devis;
import fr.eletutour.fitnesse.domain.Plat;
import fr.eletutour.fitnesse.domain.Registre;
import fr.eletutour.fitnesse.domain.Tarif;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Arrays;
import java.util.List;

@Path("/taverne")
public class TaverneResource {

    @Inject
    Carte carte;

    @Inject
    Tarif tarif;

    @Inject
    Registre registre;

    @GET
    @Path("/accueil")
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public String accueil(@QueryParam("nom") String nom) {
        if (nom == null || nom.isBlank()) {
            return "Bienvenue à la taverne, voyageur !";
        }
        return "Bienvenue à la taverne, " + nom.trim() + " !";
    }

    @GET
    @Path("/carte")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Plat> carte(@QueryParam("categorie") String categorie) {
        return carte.plats(categorie);
    }

    @GET
    @Path("/chambres")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChambreDisponible> chambres() {
        return Arrays.stream(Chambre.values())
                .map(c -> new ChambreDisponible(c.name(), c.libelle(), c.prixParNuit(), registre.disponibles(c)))
                .toList();
    }

    @GET
    @Path("/tarif")
    @Produces(MediaType.APPLICATION_JSON)
    public DevisResponse tarif(@QueryParam("chambre") String chambre,
                               @QueryParam("nuits") @DefaultValue("1") int nuits,
                               @QueryParam("membre") boolean membre) {
        Devis devis = tarif.devis(Chambre.depuis(chambre), nuits, membre);
        return new DevisResponse(devis.chambre().name(), devis.nuits(), devis.membre(),
                devis.prixDeBase(), devis.remisesLisibles(), devis.total());
    }

    public record ChambreDisponible(String code, String libelle, int prixParNuit, int disponibles) {
    }

    public record DevisResponse(String chambre, int nuits, boolean membre, int prixDeBase, String remises,
                                int total) {
    }
}
