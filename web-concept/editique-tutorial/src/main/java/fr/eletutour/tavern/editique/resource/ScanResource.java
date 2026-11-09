package fr.eletutour.tavern.editique.resource;

import fr.eletutour.tavern.editique.service.ComptoirDeScan;
import fr.eletutour.tavern.editique.service.ComptoirDeScan.Resolution;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

/**
 * Le comptoir de scan de la taverne.
 * <p>
 * {@code GET /taverne/scan} affiche un champ de saisie : une douchette USB s'y comporte
 * comme un clavier (elle tape le code puis Entrée), une appli mobile peut y coller le code
 * lu. {@code GET /taverne/scan/{code}} sert aux applis capables d'ouvrir une URL modèle.
 * Dans les deux cas, un code reconnu redirige (303) vers sa page.
 * </p>
 */
@Path("/taverne/scan")
public class ScanResource {

    @CheckedTemplate
    static class Templates {
        static native TemplateInstance comptoir(String code, String raison);
    }

    private final ComptoirDeScan comptoir;

    @Inject
    public ScanResource(ComptoirDeScan comptoir) {
        this.comptoir = comptoir;
    }

    @GET
    public Response comptoir(@QueryParam("code") String code) {
        if (code == null || code.isBlank()) {
            return html(Response.Status.OK, Templates.comptoir(null, null));
        }
        return resoudre(code);
    }

    @GET
    @Path("/{code}")
    public Response scanner(@PathParam("code") String code) {
        return resoudre(code);
    }

    private Response resoudre(String code) {
        return switch (comptoir.resoudre(code)) {
            case Resolution.Trouve trouve -> Response.seeOther(URI.create(trouve.chemin())).build();
            case Resolution.Inconnu inconnu ->
                    html(Response.Status.NOT_FOUND, Templates.comptoir(inconnu.code(), inconnu.raison()));
        };
    }

    private static Response html(Response.Status statut, TemplateInstance page) {
        return Response.status(statut)
                .entity(page.render())
                .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                .build();
    }
}
