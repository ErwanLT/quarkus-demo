package fr.eletutour.tavern.service;

import fr.eletutour.tavern.dto.TavernGreetingResponse;
import fr.eletutour.tavern.dto.TavernOrderRequest;
import fr.eletutour.tavern.dto.TavernOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TavernServiceTest {

    private TavernService tavernService;

    @BeforeEach
    void setUp() {
        tavernService = new TavernService();
    }

    @Test
    @DisplayName("Devrait saluer l'aventurier par son nom")
    void testGreetingWithName() {
        TavernGreetingResponse response = tavernService.greeting("Arthas");

        assertNotNull(response);
        assertEquals("Welcome to the tavern, Arthas!", response.message());
    }

    @Test
    @DisplayName("Devrait utiliser 'adventurer' par défaut si le nom est null")
    void testGreetingWithNullName() {
        TavernGreetingResponse response = tavernService.greeting(null);

        assertEquals("Welcome to the tavern, adventurer!", response.message());
    }

    @Test
    @DisplayName("Devrait utiliser 'adventurer' par défaut si le nom est vide")
    void testGreetingWithBlankName() {
        TavernGreetingResponse response = tavernService.greeting("   ");

        assertEquals("Welcome to the tavern, adventurer!", response.message());
    }

    @Test
    @DisplayName("Devrait enregistrer une commande et renvoyer un reçu")
    void testOrder() {
        TavernOrderRequest request = new TavernOrderRequest("Healing Potion", 3);

        TavernOrderResponse response = tavernService.order(request);

        assertNotNull(response);
        assertEquals("Healing Potion", response.item());
        assertEquals(3, response.quantity());
        assertEquals("Order accepted by the tavern keeper.", response.note());
    }
}
