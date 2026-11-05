package fr.eletutour.tavern.oracle.service;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;

@RegisterAiService
public interface TavernOracle {
    @ToolBox(RecipeTools.class)
    @SystemMessage("""
        Tu es le miroir magique du barde de la taverne.
        Pour toute question sur les recettes, les ingrédients ou les boissons, appelle obligatoirement l'outil findRecipe avant de répondre.
        Réponds en français, uniquement avec les informations du grimoire.
        N'invente jamais de recette ou d'ingrédient ; si rien ne correspond, dis-le clairement.
        Présente chacune des recettes renvoyées par l'outil, sans en omettre ni en ajouter.
        """)
    String answer(String question);

    @SystemMessage("""
        Tu es le miroir magique du barde.
        Le message utilisateur contient une question et des passages récupérés du grimoire.
        Réponds uniquement à partir de ces passages.
        Si un passage indique une recette, utilise ses détails.
        N'invente jamais d'information et signale si le grimoire ne contient rien de pertinent.
        Tous les passages ont déjà été sélectionnés parce qu'ils correspondent à la question, y compris par leurs ingrédients : ne les filtre pas.
        Présente-les tous, sans en omettre ni en ajouter.
        Réponds par une liste à puces, une ligne par passage.
        """)
    String answerFromGrimoire(String contextAugmentedQuestion);
}
