package fr.eletutour.tavern.vaadin.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@ApplicationScoped
public class TavernBroadcaster {
    private static final Logger LOG = LoggerFactory.getLogger(TavernBroadcaster.class);
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final LinkedList<Consumer<ReservationAddedEvent>> reservationListeners = new LinkedList<>();
    private final LinkedList<Consumer<StockUpdatedEvent>> stockListeners = new LinkedList<>();

    public synchronized void registerReservationListener(Consumer<ReservationAddedEvent> listener) {
        LOG.debug("Nouveau listener enregistré pour les réservations.");
        reservationListeners.add(listener);
    }

    public synchronized void unregisterReservationListener(Consumer<ReservationAddedEvent> listener) {
        LOG.debug("Listener de réservations retiré.");
        reservationListeners.remove(listener);
    }

    public synchronized void registerStockListener(Consumer<StockUpdatedEvent> listener) {
        LOG.debug("Nouveau listener enregistré pour les stocks.");
        stockListeners.add(listener);
    }

    public synchronized void unregisterStockListener(Consumer<StockUpdatedEvent> listener) {
        LOG.debug("Listener de stocks retiré.");
        stockListeners.remove(listener);
    }

    public synchronized void broadcast(ReservationAddedEvent event) {
        LOG.info("Diffusion d'une nouvelle réservation : {}", event.entry().guestName());
        for (Consumer<ReservationAddedEvent> listener : reservationListeners) {
            executor.execute(() -> listener.accept(event));
        }
    }

    public synchronized void broadcast(StockUpdatedEvent event) {
        LOG.debug("Diffusion d'une mise à jour des stocks à {} listeners.", stockListeners.size());
        for (Consumer<StockUpdatedEvent> listener : stockListeners) {
            executor.execute(() -> listener.accept(event));
        }
    }
}
