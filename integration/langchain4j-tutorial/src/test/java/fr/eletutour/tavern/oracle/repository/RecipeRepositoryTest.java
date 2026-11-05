package fr.eletutour.tavern.oracle.repository;

import fr.eletutour.tavern.oracle.model.Recipe;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestTransaction
class RecipeRepositoryTest {
    @Inject
    RecipeRepository recipes;

    private List<String> noms(String question) {
        return recipes.search(question).stream().map(recipe -> recipe.name).toList();
    }

    @Test
    @DisplayName("Un mot accentué est retrouvé dans le grimoire")
    void retrouveUnMotAccentue() {
        assertEquals(List.of("Jus de Mûres Sauvages"), noms("mûres"));
    }

    @Test
    @DisplayName("Un mot de trois lettres est pris en compte")
    void retrouveUnMotCourt() {
        assertEquals(List.of("Vin Chaud de la Garde"), noms("vin"));
    }

    @Test
    @DisplayName("Les recettes qui couvrent le plus de mots-clés arrivent en premier")
    void classeLesRecettesParPertinence() {
        List<String> resultats = noms("citron gingembre");

        assertEquals("Tonic de Mandragore", resultats.get(0));
        assertTrue(resultats.contains("Limonade des Fées"));
        assertEquals(resultats.size(), resultats.stream().distinct().count());
    }

    @Test
    @DisplayName("Une question sans mot significatif ne renvoie rien")
    void neRenvoieRienPourUneQuestionSansMotSignificatif() {
        assertTrue(recipes.search("").isEmpty());
        assertTrue(recipes.search("les des").isEmpty());
        assertTrue(recipes.search(null).isEmpty());
    }

    @Test
    @DisplayName("Une recette ajoutée au grimoire est retrouvable")
    void retrouveUneRecetteAjoutee() {
        Recipe recipe = new Recipe();
        recipe.name = "Brouet de Kobold";
        recipe.description = "Soupe épaisse.";
        recipe.ingredients = "navet, lard";
        recipes.persist(recipe);

        assertEquals(List.of("Brouet de Kobold"), noms("navet"));
    }
}
