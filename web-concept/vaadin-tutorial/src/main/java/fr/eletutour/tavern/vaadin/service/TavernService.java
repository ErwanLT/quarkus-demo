package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.CellarBoard;
import fr.eletutour.tavern.vaadin.model.DashboardSnapshot;
import fr.eletutour.tavern.vaadin.model.MenuBoard;
import fr.eletutour.tavern.vaadin.model.ReservationBoard;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import fr.eletutour.tavern.vaadin.model.ServiceBoard;
import fr.eletutour.tavern.vaadin.repository.InMemoryTavernRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TavernService {

    private final InMemoryTavernRepository repository;
    private final TavernBroadcaster broadcaster;

    @Inject
    public TavernService(InMemoryTavernRepository repository, TavernBroadcaster broadcaster) {
        this.repository = repository;
        this.broadcaster = broadcaster;
    }

    public DashboardSnapshot getDashboard() {
        return repository.fetchDashboardSnapshot();
    }

    public MenuBoard getMenuBoard() {
        return repository.fetchMenuBoard();
    }

    public ReservationBoard getReservationBoard() {
        return repository.fetchReservationBoard();
    }

    public CellarBoard getCellarBoard() {
        return repository.fetchCellarBoard();
    }

    public ServiceBoard getServiceBoard() {
        return repository.fetchServiceBoard();
    }

    public void addReservation(ReservationEntry entry) {
        repository.addReservation(entry);
        broadcaster.broadcast(new ReservationAddedEvent(entry));
    }
}
