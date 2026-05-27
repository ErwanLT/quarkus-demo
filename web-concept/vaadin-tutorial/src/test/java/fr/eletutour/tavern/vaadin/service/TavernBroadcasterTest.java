package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TavernBroadcasterTest {

    private TavernBroadcaster broadcaster;

    @BeforeEach
    void setUp() {
        broadcaster = new TavernBroadcaster();
    }

    @Test
    @DisplayName("broadcast réservation notifie tous les listeners enregistrés")
    void broadcastReservationNotifiesAllListeners() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger received = new AtomicInteger();

        Consumer<ReservationAddedEvent> listener1 = event -> { received.incrementAndGet(); latch.countDown(); };
        Consumer<ReservationAddedEvent> listener2 = event -> { received.incrementAndGet(); latch.countDown(); };

        broadcaster.registerReservationListener(listener1);
        broadcaster.registerReservationListener(listener2);

        broadcaster.broadcast(new ReservationAddedEvent(
                new ReservationEntry("Guilde", 3, "20:00", "Comptoir", "Confirmée", "test")));

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(2, received.get());
    }

    @Test
    @DisplayName("unregister retire le listener de réservations")
    void unregisterReservationListenerStopsDelivery() throws InterruptedException {
        AtomicInteger received = new AtomicInteger();
        Consumer<ReservationAddedEvent> listener = event -> received.incrementAndGet();

        broadcaster.registerReservationListener(listener);
        broadcaster.unregisterReservationListener(listener);

        broadcaster.broadcast(new ReservationAddedEvent(
                new ReservationEntry("Guilde", 3, "20:00", "Comptoir", "Confirmée", "test")));

        // Laisser un délai au cas où l'exécutor s'exécuterait quand même
        Thread.sleep(200);
        assertEquals(0, received.get());
    }

    @Test
    @DisplayName("broadcast stock notifie tous les listeners enregistrés")
    void broadcastStockNotifiesAllListeners() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger received = new AtomicInteger();

        Consumer<StockUpdatedEvent> listener1 = event -> { received.incrementAndGet(); latch.countDown(); };
        Consumer<StockUpdatedEvent> listener2 = event -> { received.incrementAndGet(); latch.countDown(); };

        broadcaster.registerStockListener(listener1);
        broadcaster.registerStockListener(listener2);

        broadcaster.broadcast(new StockUpdatedEvent(List.of(
                new CellarStock("Blonde", 10, 20, "pintes", "test"))));

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(2, received.get());
    }

    @Test
    @DisplayName("unregister retire le listener de stocks")
    void unregisterStockListenerStopsDelivery() throws InterruptedException {
        AtomicInteger received = new AtomicInteger();
        Consumer<StockUpdatedEvent> listener = event -> received.incrementAndGet();

        broadcaster.registerStockListener(listener);
        broadcaster.unregisterStockListener(listener);

        broadcaster.broadcast(new StockUpdatedEvent(List.of()));

        Thread.sleep(200);
        assertEquals(0, received.get());
    }

    @Test
    @DisplayName("broadcast sans listener enregistré ne lève pas d'erreur")
    void broadcastWithoutListenersIsSafe() {
        broadcaster.broadcast(new ReservationAddedEvent(
                new ReservationEntry("Guilde", 3, "20:00", "Comptoir", "Confirmée", "test")));
        broadcaster.broadcast(new StockUpdatedEvent(List.of()));
    }
}
