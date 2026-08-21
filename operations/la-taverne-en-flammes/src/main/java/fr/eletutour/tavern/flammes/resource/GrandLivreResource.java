package fr.eletutour.tavern.flammes.resource;

import fr.eletutour.tavern.flammes.comptes.Ecriture;
import fr.eletutour.tavern.flammes.comptes.GrandLivreService;
import fr.eletutour.tavern.flammes.resource.error.ApiProblem;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/taverne/grand-livre")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "5. Persistance", description = "Atomicite et timeout de transaction sur le Grand Livre des Comptes")
public class GrandLivreResource {

    @Inject
    GrandLivreService grandLivreService;

    @GET
    @Operation(summary = "Lire le grand livre", description = "Toutes les ecritures commitees")
    public List<Ecriture> grandLivre() {
        return grandLivreService.grandLivre();
    }

    @POST
    @Path("/ecritures")
    @Operation(
        summary = "Facturer une tournee",
        description = "Ecrit la consommation et la taxe de guilde dans une seule transaction"
    )
    @APIResponse(responseCode = "201", description = "Les deux lignes sont commitees")
    public Response facturer(
        @QueryParam("aventurier") @DefaultValue("Grimgor") String aventurier,
        @QueryParam("montant") @DefaultValue("30") int montantPiecesOr
    ) {
        List<Ecriture> ecritures = grandLivreService.enregistrerTournee(aventurier, montantPiecesOr);
        return Response.status(Response.Status.CREATED).entity(ecritures).build();
    }

    @POST
    @Path("/ecritures-interrompues")
    @Operation(
        summary = "Facturer une tournee interrompue par l'incendie",
        description = "Une poutre tombe entre les deux ecritures : JTA annule tout, le grand livre reste coherent"
    )
    @APIResponse(
        responseCode = "500",
        description = "Transaction annulee, aucune ligne conservee",
        content = @Content(schema = @Schema(implementation = ApiProblem.class))
    )
    public void facturerInterrompue(
        @QueryParam("aventurier") @DefaultValue("Grimgor") String aventurier,
        @QueryParam("montant") @DefaultValue("30") int montantPiecesOr
    ) {
        grandLivreService.enregistrerTourneeInterrompue(aventurier, montantPiecesOr);
    }

    @POST
    @Path("/ecritures-lentes")
    @Operation(
        summary = "Facturer une tournee sur une base ralentie",
        description = "La transaction est bornee a 2s par @TransactionConfiguration : au-dela, Narayana annule "
            + "plutot que de garder les verrous SQL"
    )
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Ecriture terminee dans les temps"),
        @APIResponse(
            responseCode = "503",
            description = "Timeout de transaction, ecriture annulee",
            content = @Content(schema = @Schema(implementation = ApiProblem.class))
        )
    })
    public void facturerLentement(
        @QueryParam("aventurier") @DefaultValue("Grimgor") String aventurier,
        @QueryParam("montant") @DefaultValue("30") int montantPiecesOr,
        @QueryParam("attenteMs") @DefaultValue("500") long attenteMs
    ) {
        grandLivreService.enregistrerTourneeLente(aventurier, montantPiecesOr, attenteMs);
    }

    @DELETE
    @Operation(summary = "Purger le grand livre", description = "Pour rejouer le scenario")
    public long purger() {
        return grandLivreService.purger();
    }
}
