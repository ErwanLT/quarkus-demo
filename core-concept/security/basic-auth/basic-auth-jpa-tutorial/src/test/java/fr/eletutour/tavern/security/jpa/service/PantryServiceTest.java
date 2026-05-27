package fr.eletutour.tavern.security.jpa.service;

import fr.eletutour.tavern.security.jpa.dto.StockResponse;
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
    @DisplayName("La reserve doit lister les denrees attendues")
    void testPantry() {
        StockResponse response = pantryService.pantry();

        assertEquals("pantry", response.area());
        assertEquals(4, response.items().size());
        assertTrue(response.items().contains("bread"));
    }

    @Test
    @DisplayName("La cave doit lister les boissons attendues")
    void testCellar() {
        StockResponse response = pantryService.cellar();

        assertEquals("cellar", response.area());
        assertTrue(response.items().contains("ale"));
    }
}
