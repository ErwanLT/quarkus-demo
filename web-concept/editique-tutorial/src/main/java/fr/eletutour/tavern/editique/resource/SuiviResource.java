package fr.eletutour.tavern.editique.resource;

import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.Produit;
import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import fr.eletutour.tavern.editique.service.IdentificationService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import java.util.List;

/**
 * Pages HTML vers lesquelles pointent les QR codes imprimés : suivi d'une commande,
 * suivi d'une facture, menu d'une table et fiche d'un produit.
 * <p>
 * Le document papier devient une passerelle vers l'application : le QR code n'est
 * pas une décoration, il ouvre une vraie route REST de la taverne.
 * </p>
 */
@Path("/taverne")
public class SuiviResource {

    private static final String TYPE_HTML = "text/html; charset=UTF-8";

    /** Templates Qute vérifiés à la compilation (templates/SuiviResource/*.html). */
    @CheckedTemplate
    static class Templates {
        static native TemplateInstance commande(Commande commande);

        static native TemplateInstance facture(FactureFournisseur facture);

        static native TemplateInstance menu(String table, List<Produit> produits);

        static native TemplateInstance produit(Produit produit, String ean13);
    }

    private final ArchivesTaverne archives;
    private final IdentificationService identification;

    @Inject
    public SuiviResource(ArchivesTaverne archives, IdentificationService identification) {
        this.archives = archives;
        this.identification = identification;
    }

    @GET
    @Path("/commandes/{numero}/suivi")
    @Produces(TYPE_HTML)
    public TemplateInstance suiviCommande(@PathParam("numero") String numero) {
        return archives.trouverCommande(numero)
                .map(Templates::commande)
                .orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/factures/{numero}/suivi")
    @Produces(TYPE_HTML)
    public TemplateInstance suiviFacture(@PathParam("numero") String numero) {
        return archives.trouverFacture(numero)
                .map(Templates::facture)
                .orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/menu")
    @Produces(TYPE_HTML)
    public TemplateInstance menu(@QueryParam("table") String table) {
        String tableAffichee = (table == null || table.isBlank()) ? null : table;
        return Templates.menu(tableAffichee, archives.produits());
    }

    /**
     * Fiche d'un produit, ouverte par le comptoir de scan quand on scanne son étiquette EAN-13.
     */
    @GET
    @Path("/produits/{reference}")
    @Produces(TYPE_HTML)
    public TemplateInstance produit(@PathParam("reference") String reference) {
        return archives.trouverProduit(reference)
                .map(p -> Templates.produit(p, identification.codeEan13(p.getCodeEan13Base())))
                .orElseThrow(NotFoundException::new);
    }
}
