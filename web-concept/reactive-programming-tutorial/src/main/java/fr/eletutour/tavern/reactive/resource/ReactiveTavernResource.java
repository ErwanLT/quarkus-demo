package fr.eletutour.tavern.reactive.resource;

import fr.eletutour.tavern.reactive.dto.BeerResponse;
import fr.eletutour.tavern.reactive.dto.ClientOrderResponse;
import fr.eletutour.tavern.reactive.dto.ProgrammingStyleResponse;
import fr.eletutour.tavern.reactive.service.ReactiveTavernService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.Arrays;
import java.util.List;

@Path("/taverne/reactif")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Programmation reactive", description = "Demonstration Mutiny avec le tavernier aux huit bras.")
public class ReactiveTavernResource {

    private final ReactiveTavernService reactiveTavernService;

    @Inject
    public ReactiveTavernResource(ReactiveTavernService reactiveTavernService) {
        this.reactiveTavernService = reactiveTavernService;
    }

    @GET
    @Path("/pression/{aventurier}")
    @Operation(
            summary = "Servir une chope future avec Uni",
            description = "Retourne une seule reponse future : la chope est livree quand elle est pleine."
    )
    @APIResponse(
            responseCode = "200",
            description = "Chope servie.",
            content = @Content(schema = @Schema(implementation = BeerResponse.class))
    )
    public Uni<BeerResponse> pourBeer(
            @Parameter(description = "Nom de l'aventurier.", example = "Gimli")
            @PathParam("aventurier")
            String adventurer,
            @Parameter(description = "Duree simulee du service en millisecondes.", example = "300")
            @QueryParam("durationMs")
            @DefaultValue("300")
            long durationMs
    ) {
        return reactiveTavernService.pourBeer(adventurer, durationMs);
    }

    @GET
    @Path("/pression/simultanee")
    @Operation(
            summary = "Servir plusieurs chopes en parallele avec Uni.join()",
            description = "Sert plusieurs aventuriers en meme temps : le temps total observe se rapproche du plus long "
                    + "des services, jamais de leur somme, preuve que le tavernier ne bloque jamais un bras pour un seul client."
    )
    @APIResponse(
            responseCode = "200",
            description = "Chopes servies simultanement.",
            content = @Content(schema = @Schema(implementation = BeerResponse.class))
    )
    public Uni<List<BeerResponse>> pourTournee(
            @Parameter(description = "Noms des aventuriers separes par une virgule.", example = "Gimli,Legolas,Frodon")
            @QueryParam("aventuriers")
            @DefaultValue("Gimli,Legolas,Frodon")
            String adventurersParam,
            @Parameter(description = "Duree simulee du service en millisecondes, appliquee a chaque aventurier.", example = "300")
            @QueryParam("durationMs")
            @DefaultValue("300")
            long durationMs
    ) {
        List<String> adventurers = Arrays.stream(adventurersParam.split(","))
                .toList();
        return reactiveTavernService.pourTournee(adventurers, durationMs);
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Recevoir un flux de clients avec Multi",
            description = "Retourne plusieurs commandes au fil du temps avec Server-Sent Events."
    )
    @APIResponse(
            responseCode = "200",
            description = "Flux de commandes emis.",
            content = @Content(schema = @Schema(implementation = ClientOrderResponse.class))
    )
    public Multi<ClientOrderResponse> streamClientOrders(
            @Parameter(description = "Nombre de clients a emettre.", example = "5")
            @QueryParam("count")
            @DefaultValue("5")
            int count,
            @Parameter(description = "Intervalle entre deux clients en millisecondes.", example = "250")
            @QueryParam("intervalMs")
            @DefaultValue("250")
            long intervalMs
    ) {
        return reactiveTavernService.streamClientOrders(count, intervalMs);
    }

    @GET
    @Path("/tournee")
    @Operation(
            summary = "Collecter un flux Multi en liste",
            description = "Expose le meme flux de clients, collecte en JSON pour les tests et les demonstrations curl simples."
    )
    @APIResponse(
            responseCode = "200",
            description = "Commandes collectees.",
            content = @Content(schema = @Schema(implementation = ClientOrderResponse.class))
    )
    public Uni<List<ClientOrderResponse>> collectClientOrders(
            @Parameter(description = "Nombre de clients a collecter.", example = "3")
            @QueryParam("count")
            @DefaultValue("3")
            int count,
            @Parameter(description = "Intervalle entre deux clients en millisecondes.", example = "50")
            @QueryParam("intervalMs")
            @DefaultValue("50")
            long intervalMs
    ) {
        return reactiveTavernService.collectClientOrders(count, intervalMs);
    }

    @GET
    @Path("/comparaison")
    @Operation(
            summary = "Comparer imperatif et reactif",
            description = "Resume la difference entre bloquer devant le fut et orchestrer des evenements futurs."
    )
    @APIResponse(
            responseCode = "200",
            description = "Comparaison retournee.",
            content = @Content(schema = @Schema(implementation = ProgrammingStyleResponse.class))
    )
    public ProgrammingStyleResponse compareStyles() {
        return reactiveTavernService.compareStyles();
    }
}