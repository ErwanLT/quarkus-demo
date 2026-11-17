package fr.eletutour.tavern.coursier.guilde;

import fr.eletutour.tavern.coursier.model.Contrat;
import fr.eletutour.tavern.coursier.model.ContratAccepte;
import fr.eletutour.tavern.coursier.model.Difficulte;
import fr.eletutour.tavern.coursier.exception.model.Probleme;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Guilde des aventuriers simulée.
 *
 * <p>Dans un vrai système, ce service tournerait ailleurs. Il est ici embarqué
 * pour que le tutoriel reste autonome : le coursier lui parle malgré tout en
 * HTTP, via le client Quarkus, et non par un appel Java direct.</p>
 *
 * <p>Le header {@code X-Taverne} est obligatoire : c'est la preuve que
 * {@code @ClientHeaderParam} est bien appliqué par le client déclaratif.</p>
 *
 * <p>Le contrat n°13 est maudit : le lire fait prendre feu au registre et la
 * Guilde répond 500. C'est le cas d'école d'une erreur distante que le client
 * ne sait pas traduire en erreur métier.</p>
 */
@Path("/guilde/contrats")
@Produces(MediaType.APPLICATION_JSON)
public class GuildeSimulee {

    private static final String SCEAU = "X-Taverne";
    private static final long CONTRAT_MAUDIT = 13;

    private static final List<Contrat> CONTRATS = List.of(
            new Contrat(1, "Rats dans la cave", "Brasi le tavernier", Difficulte.FACILE, 5),
            new Contrat(2, "Livraison de tonneaux à Bree", "La Guilde des marchands", Difficulte.MOYEN, 20),
            new Contrat(3, "Le dragon de la combe", "Le bourgmestre", Difficulte.MORTEL, 500));

    @GET
    public List<Contrat> contrats(@QueryParam("difficulte") Difficulte difficulte,
                                  @HeaderParam(SCEAU) String taverne) {
        exigerSceau(taverne);
        return CONTRATS.stream()
                .filter(contrat -> difficulte == null || contrat.difficulte() == difficulte)
                .toList();
    }

    @GET
    @Path("/{id}")
    public Contrat contrat(@PathParam("id") long id, @HeaderParam(SCEAU) String taverne) {
        exigerSceau(taverne);
        return trouver(id);
    }

    @POST
    @Path("/{id}/acceptation")
    public ContratAccepte accepter(@PathParam("id") long id,
                                   @HeaderParam(SCEAU) String taverne,
                                   @HeaderParam("X-Coursier") String coursier,
                                   @HeaderParam("X-Request-Id") String bordereau) {
        exigerSceau(taverne);
        if (coursier == null || coursier.isBlank()) {
            throw arret(400, "Coursier manquant",
                    "Le header X-Coursier est obligatoire pour signer un contrat.");
        }
        Contrat contrat = trouver(id);
        return new ContratAccepte(contrat.id(), contrat.titre(), coursier, contrat.recompense(), bordereau);
    }

    private void exigerSceau(String taverne) {
        if (taverne == null || taverne.isBlank()) {
            throw arret(400, "Sceau manquant",
                    "Le header X-Taverne est obligatoire : la Guilde ne parle pas aux inconnus.");
        }
    }

    private Contrat trouver(long id) {
        if (id == CONTRAT_MAUDIT) {
            throw arret(500, "Registre en flammes",
                    "Le registre de la Guilde a pris feu à la lecture du contrat 13.");
        }
        return CONTRATS.stream()
                .filter(contrat -> contrat.id() == id)
                .findFirst()
                .orElseThrow(() -> arret(404, "Contrat introuvable",
                        "Aucun contrat ne porte le numéro " + id + "."));
    }

    private WebApplicationException arret(int status, String title, String detail) {
        return new WebApplicationException(Probleme.reponse(status, title, detail));
    }
}
