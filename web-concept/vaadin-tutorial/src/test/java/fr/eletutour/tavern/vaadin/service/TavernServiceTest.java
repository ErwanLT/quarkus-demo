package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.AnalyticsData;
import fr.eletutour.tavern.vaadin.model.CellarBoard;
import fr.eletutour.tavern.vaadin.model.DashboardSnapshot;
import fr.eletutour.tavern.vaadin.model.MenuBoard;
import fr.eletutour.tavern.vaadin.model.ReservationBoard;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import fr.eletutour.tavern.vaadin.model.ServiceBoard;
import fr.eletutour.tavern.vaadin.repository.InMemoryTavernRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TavernServiceTest {

    private InMemoryTavernRepository repository;
    private TavernBroadcaster broadcaster;
    private TavernService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTavernRepository();
        broadcaster = new TavernBroadcaster();
        service = new TavernService(repository, broadcaster);
    }

    @Test
    @DisplayName("getDashboard délègue au repository")
    void getDashboard() {
        DashboardSnapshot snapshot = service.getDashboard();
        assertNotNull(snapshot);
        assertNotNull(snapshot.heroTitle());
    }

    @Test
    @DisplayName("getMenuBoard délègue au repository")
    void getMenuBoard() {
        MenuBoard board = service.getMenuBoard();
        assertEquals(3, board.sections().size());
    }

    @Test
    @DisplayName("getReservationBoard délègue au repository")
    void getReservationBoard() {
        ReservationBoard board = service.getReservationBoard();
        assertEquals(4, board.reservations().size());
    }

    @Test
    @DisplayName("getCellarBoard délègue au repository")
    void getCellarBoard() {
        CellarBoard board = service.getCellarBoard();
        assertEquals(4, board.stocks().size());
    }

    @Test
    @DisplayName("getServiceBoard délègue au repository")
    void getServiceBoard() {
        ServiceBoard board = service.getServiceBoard();
        assertEquals(4, board.serviceMetrics().size());
    }

    @Test
    @DisplayName("getMapLocations délègue au repository")
    void getMapLocations() {
        assertEquals(4, service.getMapLocations().size());
    }

    @Test
    @DisplayName("getRevenueData renvoie les recettes de la semaine")
    void getRevenueData() {
        AnalyticsData data = service.getRevenueData();
        assertEquals("bar", data.chartType());
    }

    @Test
    @DisplayName("getDrinkPopularity renvoie la popularité des boissons")
    void getDrinkPopularity() {
        AnalyticsData data = service.getDrinkPopularity();
        assertEquals("pie", data.chartType());
    }

    @Test
    @DisplayName("addReservation persiste l'entrée et notifie les listeners")
    void addReservationBroadcasts() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ReservationAddedEvent> received = new AtomicReference<>();
        broadcaster.registerReservationListener(event -> {
            received.set(event);
            latch.countDown();
        });

        ReservationEntry entry = new ReservationEntry(
                "Aventuriers de l'Aube", 4, "20:00", "Coin du feu", "Confirmée", "Test");
        service.addReservation(entry);

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Le listener n'a pas été notifié à temps");
        assertEquals(entry, received.get().entry());

        List<ReservationEntry> reservations = service.getReservationBoard().reservations();
        assertTrue(reservations.contains(entry));
    }
}
