package fr.eletutour.tavern.vaadin.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@ApplicationScoped
public class TavernBroadcaster {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final LinkedList<Consumer<ReservationAddedEvent>> listeners = new LinkedList<>();

    public synchronized void register(Consumer<ReservationAddedEvent> listener) {
        listeners.add(listener);
    }

    public synchronized void unregister(Consumer<ReservationAddedEvent> listener) {
        listeners.remove(listener);
    }

    public synchronized void broadcast(ReservationAddedEvent event) {
        for (Consumer<ReservationAddedEvent> listener : listeners) {
            executor.execute(() -> listener.accept(event));
        }
    }
}
