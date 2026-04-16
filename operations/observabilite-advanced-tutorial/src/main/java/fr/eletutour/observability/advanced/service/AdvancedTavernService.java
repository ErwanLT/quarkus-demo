package fr.eletutour.observability.advanced.service;

import fr.eletutour.observability.advanced.metrics.BusinessTimed;
import fr.eletutour.observability.advanced.model.OrderResponse;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AdvancedTavernService {

    private static final Logger LOG = Logger.getLogger(AdvancedTavernService.class);

    @BusinessTimed(value = "tavern.advanced.order", description = "Drink order processing")
    public OrderResponse placeOrder(String drink) {
        String finalDrink = (drink == null || drink.isBlank()) ? "ale" : drink;
        return brewDrink(finalDrink);
    }

    @WithSpan("advanced-brew-drink")
    OrderResponse brewDrink(String drink) {
        Span.current().setAttribute("tavern.advanced.drink", drink);
        simulateBrew(drink);
        LOG.infof("Order served drink=%s", drink);
        return new OrderResponse(drink, "SERVED", Instant.now());
    }

    private void simulateBrew(String drink) {
        long brewDelayMs = "dragonfire".equalsIgnoreCase(drink) ? 1200L : 75L;
        try {
            Thread.sleep(brewDelayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
