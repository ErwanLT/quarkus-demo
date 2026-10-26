package fr.eletutour.tavern.editique.resource;

import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import fr.eletutour.tavern.editique.service.ImprimerieService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Point d'entrée REST de l'imprimerie de la taverne : tickets de caisse et factures fournisseurs.
 * <p>
 * Un numéro inconnu des archives renvoie un 404.
 * </p>
 */
@Path("/taverne")
public class ImprimerieResource {

    /** Les archives de démonstration couvrent ce mois-ci. */
    static final String PERIODE_COURANTE = "Octobre 2026";

    static final String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ArchivesTaverne archives;
    private final ImprimerieService imprimerie;

    @Inject
    public ImprimerieResource(ArchivesTaverne archives, ImprimerieService imprimerie) {
        this.archives = archives;
        this.imprimerie = imprimerie;
    }

    /**
     * Ticket de caisse d'une commande, affiché directement (PDF).
     */
    @GET
    @Path("/commandes/{numero}/ticket")
    @Produces("application/pdf")
    public Response ticket(@PathParam("numero") String numero) {
        return archives.trouverCommande(numero)
                .map(commande -> Response.ok(imprimerie.imprimerTicket(commande))
                        .type("application/pdf")
                        .header("Content-Disposition", "inline; filename=\"" + nomDeFichier("ticket", commande.getNumero(), "pdf") + "\"")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Liste des factures des fournisseurs.
     */
    @GET
    @Path("/factures")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FactureFournisseur> factures() {
        return archives.factures();
    }

    /**
     * Facture d'un fournisseur en PDF (par défaut) ou en XLSX avec {@code ?format=xlsx}.
     */
    @GET
    @Path("/factures/{numero}")
    @Produces({"application/pdf", TYPE_XLSX})
    public Response facture(@PathParam("numero") String numero,
                            @QueryParam("format") @DefaultValue("pdf") String format) {
        return archives.trouverFacture(numero)
                .map(facture -> "xlsx".equalsIgnoreCase(format)
                        ? Response.ok(imprimerie.exporterFactureXlsx(facture))
                                .type(TYPE_XLSX)
                                .header("Content-Disposition", "attachment; filename=\""
                                        + nomDeFichier("facture", facture.getNumero(), "xlsx") + "\"")
                                .build()
                        : Response.ok(imprimerie.imprimerFacture(facture))
                                .type("application/pdf")
                                .header("Content-Disposition", "attachment; filename=\""
                                        + nomDeFichier("facture", facture.getNumero(), "pdf") + "\"")
                                .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Relevé mensuel des fournisseurs, affiché directement (PDF).
     */
    @GET
    @Path("/fournisseurs/releve")
    @Produces("application/pdf")
    public Response releve() {
        return Response.ok(imprimerie.imprimerReleve(archives.releveFournisseurs(), PERIODE_COURANTE))
                .type("application/pdf")
                .header("Content-Disposition", "inline; filename=\"releve-fournisseurs.pdf\"")
                .build();
    }

    /**
     * Construit le nom du fichier envoyé dans l'en-tête {@code Content-Disposition}.
     * <p>
     * Seuls les caractères {@code [A-Za-z0-9-]} du numéro sont conservés, les autres
     * (guillemets, barres obliques, retours à la ligne...) sont remplacés par {@code _} :
     * un numéro farfelu ne peut ainsi ni casser l'en-tête ni désigner un chemin.
     * </p>
     */
    static String nomDeFichier(String prefixe, String numero, String extension) {
        String numeroAssaini = numero == null ? "" : numero.replaceAll("[^A-Za-z0-9-]", "_");
        return prefixe + "-" + numeroAssaini + "." + extension;
    }
}
