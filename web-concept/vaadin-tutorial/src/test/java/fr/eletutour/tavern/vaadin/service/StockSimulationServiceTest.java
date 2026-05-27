package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.repository.InMemoryTavernRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockSimulationServiceTest {

    private InMemoryTavernRepository repository;
    private TavernBroadcaster broadcaster;
    private StockSimulationService simulation;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTavernRepository();
        broadcaster = new TavernBroadcaster();
        simulation = new StockSimulationService(repository, broadcaster);
    }

    @Test
    @DisplayName("simulateStockChanges modifie les stocks et diffuse une mise à jour")
    void simulateStockChangesBroadcastsUpdate() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<StockUpdatedEvent> received = new AtomicReference<>();
        broadcaster.registerStockListener(event -> {
            received.set(event);
            latch.countDown();
        });

        simulation.simulateStockChanges();

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Aucun événement de stock reçu");
        StockUpdatedEvent event = received.get();
        assertNotNull(event);
        assertTrue(event.stocks().size() >= 4);
    }

    @Test
    @DisplayName("simulateStockChanges déclenche un remplissage quand le stock est critique")
    void simulateStockChangesRefillsLowStock() {
        // Vider un fût pour qu'il déclenche la branche de remplissage (currentLevel < maxLevel * 0.2)
        repository.updateStock("Blonde des remparts", -60);
        CellarStock empty = repository.getCellarStocks().stream()
                .filter(s -> s.productName().equals("Blonde des remparts"))
                .findFirst().orElseThrow();
        assertTrue(empty.currentLevel() <= empty.maxLevel() * 0.2);

        simulation.simulateStockChanges();

        CellarStock refilled = repository.getCellarStocks().stream()
                .filter(s -> s.productName().equals("Blonde des remparts"))
                .findFirst().orElseThrow();
        // Après simulation: si consommation a eu lieu (prob 0.7), le refill remonte à maxLevel,
        // sinon le stock reste sous le seuil et le refill atteint aussi maxLevel.
        // Dans les deux cas le stock final doit être > 0.2 * maxLevel.
        assertTrue(refilled.currentLevel() > refilled.maxLevel() * 0.2,
                "Le stock devrait avoir été remonté");
    }

    @Test
    @DisplayName("plusieurs cycles successifs ne lèvent pas d'exception")
    void multipleCyclesAreStable() {
        for (int i = 0; i < 20; i++) {
            simulation.simulateStockChanges();
        }
        // Tous les stocks doivent rester dans [0, maxLevel]
        repository.getCellarStocks().forEach(s -> {
            assertTrue(s.currentLevel() >= 0);
            assertTrue(s.currentLevel() <= s.maxLevel());
        });
    }
}
