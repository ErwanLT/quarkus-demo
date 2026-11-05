package fr.eletutour.tavern.oracle.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeTokensTest {
    @Test
    @DisplayName("Les lettres accentuées ne coupent pas les mots")
    void gardeLesAccents() {
        assertEquals(List.of("mûres", "thé", "crème"), RecipeRepository.tokens("Mûres, thé & crème !"));
    }

    @Test
    @DisplayName("Les mots vides et les doublons sont écartés")
    void ecarteMotsVidesEtDoublons() {
        assertEquals(List.of("boissons", "miel"), RecipeRepository.tokens("Les boissons avec du miel, du MIEL"));
    }

    @Test
    @DisplayName("Une question nulle ou vide ne donne aucun mot-clé")
    void gereLesQuestionsVides() {
        assertEquals(List.of(), RecipeRepository.tokens(null));
        assertEquals(List.of(), RecipeRepository.tokens("  ?! "));
    }
}
