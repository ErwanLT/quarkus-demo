package fr.eletutour.tavern.versioning.resource;

import fr.eletutour.tavern.versioning.dto.MenuResponse;
import fr.eletutour.tavern.versioning.dto.MenuV1Response;
import fr.eletutour.tavern.versioning.dto.MenuV2Response;
import fr.eletutour.tavern.versioning.dto.ProblemDetailsResponse;
import fr.eletutour.tavern.versioning.service.TavernMenuService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/taverne/versioning")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "API Versioning", description = "Demonstrations des strategies de versioning d'API REST dans la taverne.")
public class TavernVersioningResource {

    public static final String API_VERSION_HEADER = "X-Api-Version";
    public static final String MENU_V1_MEDIA_TYPE = "application/vnd.tavern.menu.v1+json";
    public static final String MENU_V2_MEDIA_TYPE = "application/vnd.tavern.menu.v2+json";

    private final TavernMenuService tavernMenuService;

    @Inject
    public TavernVersioningResource(TavernMenuService tavernMenuService) {
        this.tavernMenuService = tavernMenuService;
    }

    @GET
    @Path("/path/v1/menu")
    @Operation(
            summary = "Lire le menu V1 via le path",
            description = "Expose le contrat historique en placant la version directement dans l'URL."
    )
    @APIResponse(
            responseCode = "200",
            description = "Menu V1 retourne avec succes.",
            content = @Content(schema = @Schema(implementation = MenuV1Response.class))
    )
    public Response menuByPathV1() {
        return Response.ok(tavernMenuService.serveHistoricalMenu()).build();
    }

    @GET
    @Path("/path/v2/menu")
    @Operation(
            summary = "Lire le menu V2 via le path",
            description = "Expose le contrat enrichi en placant la version directement dans l'URL."
    )
    @APIResponse(
            responseCode = "200",
            description = "Menu V2 retourne avec succes.",
            content = @Content(schema = @Schema(implementation = MenuV2Response.class))
    )
    public Response menuByPathV2() {
        return Response.ok(tavernMenuService.serveGuildMenu()).build();
    }

    @GET
    @Path("/parameter/menu")
    @Operation(
            summary = "Lire le menu via un query parameter de version",
            description = "Selectionne le contrat V1 ou V2 avec le parametre version. La V1 est utilisee par defaut."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Menu retourne avec succes.",
                    content = @Content(schema = @Schema(oneOf = {MenuV1Response.class, MenuV2Response.class}))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Version demandee inconnue.",
                    content = @Content(
                            mediaType = UnknownMenuVersionExceptionMapper.PROBLEM_JSON,
                            schema = @Schema(implementation = ProblemDetailsResponse.class)
                    )
            )
    })
    public Response menuByParameter(
            @Parameter(description = "Version du contrat de menu.", example = "2")
            @QueryParam("version")
            @DefaultValue("1")
            String version
    ) {
        return menuForVersion(version);
    }

    @GET
    @Path("/header/menu")
    @Operation(
            summary = "Lire le menu via un header de version",
            description = "Selectionne le contrat V1 ou V2 avec le header X-Api-Version. La V1 est utilisee par defaut."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Menu retourne avec succes.",
                    content = @Content(schema = @Schema(oneOf = {MenuV1Response.class, MenuV2Response.class}))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Version demandee inconnue.",
                    content = @Content(
                            mediaType = UnknownMenuVersionExceptionMapper.PROBLEM_JSON,
                            schema = @Schema(implementation = ProblemDetailsResponse.class)
                    )
            )
    })
    public Response menuByHeader(
            @Parameter(description = "Version du contrat de menu.", example = "2")
            @HeaderParam(API_VERSION_HEADER)
            @DefaultValue("1")
            String version
    ) {
        return menuForVersion(version);
    }

    @GET
    @Path("/negotiation/menu")
    @Produces(MENU_V1_MEDIA_TYPE)
    @Operation(
            summary = "Lire le menu V1 via content negotiation",
            description = "Retourne la representation V1 lorsque le client demande le media type vendor V1."
    )
    @APIResponse(
            responseCode = "200",
            description = "Representation V1 retournee avec succes.",
            content = @Content(
                    mediaType = MENU_V1_MEDIA_TYPE,
                    schema = @Schema(implementation = MenuV1Response.class)
            )
    )
    public Response menuByMediaTypeV1() {
        return Response.ok(tavernMenuService.serveHistoricalMenu(), MENU_V1_MEDIA_TYPE).build();
    }

    @GET
    @Path("/negotiation/menu")
    @Produces(MENU_V2_MEDIA_TYPE)
    @Operation(
            summary = "Lire le menu V2 via content negotiation",
            description = "Retourne la representation V2 lorsque le client demande le media type vendor V2."
    )
    @APIResponse(
            responseCode = "200",
            description = "Representation V2 retournee avec succes.",
            content = @Content(
                    mediaType = MENU_V2_MEDIA_TYPE,
                    schema = @Schema(implementation = MenuV2Response.class)
            )
    )
    public Response menuByMediaTypeV2() {
        return Response.ok(tavernMenuService.serveGuildMenu(), MENU_V2_MEDIA_TYPE).build();
    }

    private Response menuForVersion(String version) {
        MenuResponse menu = tavernMenuService.serveMenuForVersion(version);
        return Response.ok(menu).build();
    }
}
