package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.repository.InMemoryTavernRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class StockSimulationService {

    private static final Logger LOG = LoggerFactory.getLogger(StockSimulationService.class);
    private final InMemoryTavernRepository repository;
    private final TavernBroadcaster broadcaster;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public StockSimulationService(InMemoryTavernRepository repository, TavernBroadcaster broadcaster) {
        this.repository = repository;
        this.broadcaster = broadcaster;
    }

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Démarrage de la simulation des stocks de la cave...");
        scheduler.scheduleAtFixedRate(this::simulateStockChanges, 5, 10, TimeUnit.SECONDS);
    }

    private void simulateStockChanges() {
        LOG.debug("Simulation d'un cycle de consommation...");
        for (CellarStock stock : repository.getCellarStocks()) {
            // Simulate consumption
            if (random.nextDouble() < 0.7) {
                int consumption = random.nextInt(3) + 1;
                repository.updateStock(stock.productName(), -consumption);
                LOG.info("Consommation : {} (-{} {})", stock.productName(), consumption, stock.unit());
            }

            // Simulate refill if low
            CellarStock updatedStock = repository.getCellarStocks().stream()
                    .filter(s -> s.productName().equals(stock.productName()))
                    .findFirst()
                    .orElse(stock);
            
            if (updatedStock.currentLevel() < updatedStock.maxLevel() * 0.2) {
                int refillAmount = updatedStock.maxLevel() - updatedStock.currentLevel();
                repository.updateStock(stock.productName(), refillAmount);
                LOG.info("Remplissage : {} (+{} {}) - Seuil critique atteint", stock.productName(), refillAmount, stock.unit());
            }
        }
        broadcaster.broadcast(new StockUpdatedEvent(repository.getCellarStocks()));
    }
}
