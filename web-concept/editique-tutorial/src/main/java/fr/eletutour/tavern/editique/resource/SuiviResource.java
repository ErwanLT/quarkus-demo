package fr.eletutour.tavern.editique.resource;

import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.LigneCommande;
import fr.eletutour.tavern.editique.model.LigneFacture;
import fr.eletutour.tavern.editique.model.Produit;
import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

/**
 * Pages HTML vers lesquelles pointent les QR codes imprimés : suivi d'une commande,
 * suivi d'une facture et menu d'une table.
 * <p>
 * Le document papier devient une passerelle vers l'application : le QR code n'est
 * pas une décoration, il ouvre une vraie route REST de la taverne.
 * </p>
 */
@Path("/taverne")
public class SuiviResource {

    private static final String TYPE_HTML = "text/html; charset=UTF-8";

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ArchivesTaverne archives;

    @Inject
    public SuiviResource(ArchivesTaverne archives) {
        this.archives = archives;
    }

    @GET
    @Path("/commandes/{numero}/suivi")
    @Produces(TYPE_HTML)
    public Response suiviCommande(@PathParam("numero") String numero) {
        return archives.trouverCommande(numero)
                .map(this::pageCommande)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/factures/{numero}/suivi")
    @Produces(TYPE_HTML)
    public Response suiviFacture(@PathParam("numero") String numero) {
        return archives.trouverFacture(numero)
                .map(this::pageFacture)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/menu")
    @Produces(TYPE_HTML)
    public Response menu(@QueryParam("table") String table) {
        StringBuilder contenu = new StringBuilder();
        contenu.append("<h1>La carte de The Falling Whale</h1>");
        if (table != null && !table.isBlank()) {
            contenu.append("<p class=\"table\">Carte de la ").append(echapper(table)).append("</p>");
        }
        contenu.append("<table><thead><tr><th>Produit</th><th>Référence</th><th class=\"prix\">Prix</th></tr></thead><tbody>");
        for (Produit produit : archives.produits()) {
            contenu.append("<tr><td>").append(echapper(produit.getDesignation())).append("</td>")
                    .append("<td>").append(echapper(produit.getReference())).append("</td>")
                    .append("<td class=\"prix\">").append(montant(produit.getPrixUnitaire()))
                    .append(" / ").append(echapper(produit.getUnite())).append("</td></tr>");
        }
        contenu.append("</tbody></table>");
        contenu.append("<p class=\"pied\">Une chope à la main, un parchemin dans l'autre.</p>");
        return html("La carte", contenu.toString());
    }

    private Response pageCommande(Commande commande) {
        StringBuilder contenu = new StringBuilder();
        contenu.append("<h1>Commande ").append(echapper(commande.getNumero())).append("</h1>");
        contenu.append("<p class=\"table\">").append(echapper(commande.getTable()))
                .append(" — servie par ").append(echapper(commande.getServeur()))
                .append(" le ").append(FORMAT_DATE.format(commande.getDate())).append("</p>");
        contenu.append("<table><thead><tr><th>Article</th><th>Qté</th><th class=\"prix\">Montant</th></tr></thead><tbody>");
        for (LigneCommande ligne : commande.getLignes()) {
            contenu.append("<tr><td>").append(echapper(ligne.getLibelle())).append("</td>")
                    .append("<td>").append(ligne.getQuantite()).append("</td>")
                    .append("<td class=\"prix\">").append(montant(ligne.getMontant())).append("</td></tr>");
        }
        contenu.append("</tbody><tfoot><tr><th colspan=\"2\">Total</th><th class=\"prix\">")
                .append(montant(commande.getTotal())).append("</th></tr></tfoot></table>");
        contenu.append("<p><a href=\"/taverne/commandes/").append(echapper(commande.getNumero()))
                .append("/ticket\">Télécharger le ticket de caisse (PDF)</a></p>");
        return html("Suivi de commande", contenu.toString());
    }

    private Response pageFacture(FactureFournisseur facture) {
        StringBuilder contenu = new StringBuilder();
        contenu.append("<h1>Facture ").append(echapper(facture.getNumero())).append("</h1>");
        contenu.append("<p class=\"table\">").append(echapper(facture.getFournisseur().getNom()))
                .append(" — échéance du ")
                .append(facture.getDateEcheance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append("</p>");
        contenu.append("<table><thead><tr><th>Référence</th><th>Désignation</th><th class=\"prix\">Montant HT</th></tr></thead><tbody>");
        for (LigneFacture ligne : facture.getLignes()) {
            contenu.append("<tr><td>").append(echapper(ligne.getReference())).append("</td>")
                    .append("<td>").append(echapper(ligne.getDesignation())).append("</td>")
                    .append("<td class=\"prix\">").append(montant(ligne.getMontantHt())).append("</td></tr>");
        }
        contenu.append("</tbody><tfoot>")
                .append("<tr><th colspan=\"2\">Total HT</th><th class=\"prix\">").append(montant(facture.getTotalHt())).append("</th></tr>")
                .append("<tr><th colspan=\"2\">TVA</th><th class=\"prix\">").append(montant(facture.getTotalTva())).append("</th></tr>")
                .append("<tr><th colspan=\"2\">Total TTC</th><th class=\"prix\">").append(montant(facture.getTotalTtc())).append("</th></tr>")
                .append("</tfoot></table>");
        contenu.append("<p><a href=\"/taverne/factures/").append(echapper(facture.getNumero()))
                .append("\">Télécharger la facture (PDF)</a></p>");
        return html("Suivi de facture", contenu.toString());
    }

    private static Response html(String titre, String contenu) {
        String page = """
                <!doctype html>
                <html lang="fr">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s — The Falling Whale</title>
                  <style>
                    body { margin: 0; padding: 24px; background: #2B2118; color: #F3EAD6;
                           font-family: Georgia, 'Times New Roman', serif; }
                    main { max-width: 720px; margin: 0 auto; background: #FBF7EC; color: #2B2118;
                           padding: 28px; border: 2px solid #5A4630; border-radius: 4px; }
                    h1 { margin-top: 0; color: #4A3720; }
                    p.table { font-style: italic; color: #5A4630; }
                    table { width: 100%%; border-collapse: collapse; margin: 18px 0; }
                    th, td { padding: 8px 6px; border-bottom: 1px solid #D8C9A8; text-align: left; }
                    .prix { text-align: right; white-space: nowrap; }
                    tfoot th { border-top: 2px solid #5A4630; }
                    a { color: #7A5A2E; }
                    p.pied { margin-bottom: 0; font-style: italic; color: #5A4630; }
                  </style>
                </head>
                <body><main>%s</main></body>
                </html>
                """.formatted(titre, contenu);
        return Response.ok(page).header(HttpHeaders.CONTENT_TYPE, TYPE_HTML).build();
    }

    private static String montant(BigDecimal montant) {
        return montant.setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
                .replace('.', ',') + " po";
    }

    private static String echapper(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
