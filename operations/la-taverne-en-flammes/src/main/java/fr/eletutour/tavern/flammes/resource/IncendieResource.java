package fr.eletutour.tavern.flammes.resource;

import fr.eletutour.tavern.flammes.sante.EtatTaverne;
import fr.eletutour.tavern.flammes.sante.EvenementIncendie;
import fr.eletutour.tavern.flammes.sante.IncendieService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/taverne/incendie")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "2. Sondes de sante", description = "Declencher l'incident et observer /q/health/live et /q/health/ready")
public class IncendieResource {

    @Inject
    IncendieService incendieService;

    @GET
    @Operation(summary = "Etat de la taverne", description = "Ce que voient les sondes de sante")
    @APIResponse(
        responseCode = "200",
        description = "Etat courant",
        content = @Content(schema = @Schema(implementation = EtatTaverne.class))
    )
    public EtatTaverne etat() {
        return incendieService.etat();
    }

    @POST
    @Operation(
        summary = "Mettre le feu a la cuisine",
        description = "La readiness passe DOWN : la taverne tient debout mais ne peut plus servir"
    )
    public EtatTaverne declencher(@QueryParam("origine") String origine) {
        return incendieService.declencherIncendie(origine);
    }

    @POST
    @Path("/extinctions")
    @Operation(summary = "Eteindre l'incendie", description = "La readiness peut repasser UP")
    public EtatTaverne eteindre() {
        return incendieService.eteindreIncendie();
    }

    @POST
    @Path("/effondrements")
    @Operation(
        summary = "Faire ceder la charpente",
        description = "La liveness passe DOWN : l'orchestrateur doit detruire l'instance"
    )
    public EtatTaverne effondrer() {
        return incendieService.effondrerCharpente();
    }

    @POST
    @Path("/reconstructions")
    @Operation(summary = "Relever la charpente", description = "La liveness repasse UP")
    public EtatTaverne relever() {
        return incendieService.releverCharpente();
    }

    @GET
    @Path("/chronologie")
    @Operation(summary = "Lire la chronologie", description = "Les evenements horodates de l'incident")
    public List<EvenementIncendie> chronologie() {
        return incendieService.chronologie();
    }

    @DELETE
    @Operation(summary = "Rejouer le scenario", description = "Remet l'etat et la chronologie a zero")
    public EtatTaverne reinitialiser() {
        return incendieService.reinitialiser();
    }
}
