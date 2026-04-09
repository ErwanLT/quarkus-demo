package fr.eletutour.observability.service;

import fr.eletutour.observability.model.OrderResponse;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import org.jboss.logging.Logger;
import fr.eletutour.observability.metrics.BusinessTimed;

@ApplicationScoped
public class TavernService {

    private static final Logger LOG = Logger.getLogger(TavernService.class);

    @BusinessTimed(value = "tavern.order", description = "Drink order processing")
    public OrderResponse placeOrder(String drink) {
        String finalDrink = (drink == null || drink.isBlank()) ? "ale" : drink;
        return brewDrink(finalDrink);
    }

    @WithSpan("brew-drink")
    OrderResponse brewDrink(String drink) {
        Span.current().setAttribute("tavern.drink", drink);
        simulateBrew();
        LOG.infof("Order served drink=%s", drink);
        return new OrderResponse(drink, "SERVED", Instant.now());
    }

    private void simulateBrew() {
        try {
            Thread.sleep(75L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
