package fr.eletutour.database.panache.resource;

import fr.eletutour.database.panache.dto.IngredientDTO;
import fr.eletutour.database.panache.dto.RecipeDTO;
import fr.eletutour.database.panache.service.GrimoireService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/grimoire")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Grimoire de la Taverne", description = "Gestion des ingrédients et des recettes magiques")
public class GrimoireResource {

    @Inject
    GrimoireService grimoireService;

    @GET
    @Path("/ingredients")
    @Operation(summary = "Lister tous les ingrédients", description = "Retourne la liste complète des ingrédients disponibles dans la réserve.")
    public List<IngredientDTO> allIngredients() {
        return grimoireService.consulterLaReserve();
    }

    @GET
    @Path("/ingredients/paged")
    @Operation(summary = "Lister les ingrédients avec pagination", description = "Retourne une page d'ingrédients.")
    public List<IngredientDTO> pagedIngredients(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("2") int size) {
        return grimoireService.feuilleterLaReserve(page, size);
    }

    @POST
    @Path("/ingredients")
    @Operation(summary = "Ajouter un ingrédient", description = "Ajoute un nouvel ingrédient à la réserve.")
    @APIResponse(responseCode = "201", description = "Ingrédient créé", 
                 content = @Content(schema = @Schema(implementation = IngredientDTO.class)))
    @APIResponse(responseCode = "400", description = "Données d'entrée invalides")
    public Response addIngredient(IngredientDTO dto) {
        try {
            IngredientDTO created = grimoireService.acquerirNouvelIngredient(dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/recipes")
    @Operation(summary = "Lister toutes les recettes", description = "Retourne la liste complète des recettes du grimoire.")
    public List<RecipeDTO> allRecipes() {
        return grimoireService.consulterLeGrimoire();
    }

    @GET
    @Path("/recipes/search")
    @Operation(summary = "Rechercher des recettes", description = "Recherche des recettes par titre.")
    public List<RecipeDTO> searchRecipes(@QueryParam("title") String title) {
        return grimoireService.chercherDansLeGrimoire(title);
    }

    @POST
    @Path("/recipes")
    @Operation(summary = "Ajouter une recette", description = "Ajoute une nouvelle recette au grimoire.")
    @APIResponse(responseCode = "201", description = "Recette créée", 
                 content = @Content(schema = @Schema(implementation = RecipeDTO.class)))
    @APIResponse(responseCode = "400", description = "Données d'entrée invalides")
    public Response addRecipe(RecipeDTO dto) {
        try {
            RecipeDTO created = grimoireService.inscrireNouvelleRecette(dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
