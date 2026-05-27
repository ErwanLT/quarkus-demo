package fr.eletutour.tavern.security.service;

import fr.eletutour.tavern.security.dto.StockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PantryServiceTest {

    private PantryService pantryService;

    @BeforeEach
    void setUp() {
        pantryService = new PantryService();
    }

    @Test
    @DisplayName("La reserve doit contenir les denrees attendues")
    void testPantry() {
        StockResponse response = pantryService.pantry();

        assertEquals("pantry", response.area());
        assertTrue(response.items().contains("bread"));
        assertTrue(response.items().contains("cheese"));
        assertEquals(4, response.items().size());
    }

    @Test
    @DisplayName("La cave doit contenir les biere et hydromel attendus")
    void testCellar() {
        StockResponse response = pantryService.cellar();

        assertEquals("cellar", response.area());
        assertTrue(response.items().contains("ale"));
        assertTrue(response.items().contains("mead"));
        assertTrue(response.items().contains("stout"));
    }
}
