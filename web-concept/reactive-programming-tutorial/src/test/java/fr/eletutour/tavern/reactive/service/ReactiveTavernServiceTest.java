package fr.eletutour.tavern.reactive.service;

import fr.eletutour.tavern.reactive.dto.BeerResponse;
import fr.eletutour.tavern.reactive.dto.ClientOrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReactiveTavernServiceTest {

    private final ReactiveTavernService reactiveTavernService = new ReactiveTavernService();

    @Test
    @DisplayName("Devrait servir une seule chope future avec Uni")
    void shouldServeOneFutureBeerWithUni() {
        BeerResponse response = reactiveTavernService.pourBeer(" Gimli ", 10)
                .await()
                .indefinitely();

        assertEquals("Gimli", response.adventurer());
        assertEquals(10, response.durationMs());
        assertTrue(response.message().contains("chope est pleine"));
    }

    @Test
    @DisplayName("Devrait emettre plusieurs clients avec Multi")
    void shouldStreamSeveralClientsWithMulti() {
        List<ClientOrderResponse> responses = reactiveTavernService.streamClientOrders(3, 10)
                .collect()
                .asList()
                .await()
                .indefinitely();

        assertEquals(3, responses.size());
        assertEquals(1, responses.getFirst().sequence());
        assertEquals("Client 3", responses.get(2).client());
    }

    @Test
    @DisplayName("Devrait borner les parametres pour garder une demonstration lisible")
    void shouldSanitizeInvalidParameters() {
        BeerResponse beerResponse = reactiveTavernService.pourBeer("", -1)
                .await()
                .indefinitely();
        List<ClientOrderResponse> clientOrders = reactiveTavernService.streamClientOrders(0, 10)
                .collect()
                .asList()
                .await()
                .indefinitely();

        assertEquals("Aventurier anonyme", beerResponse.adventurer());
        assertEquals(300, beerResponse.durationMs());
        assertEquals(5, clientOrders.size());
    }
}
