package fr.eletutour.tavern.vaadin.repository;

import fr.eletutour.tavern.vaadin.model.AnalyticsData;
import fr.eletutour.tavern.vaadin.model.CellarBoard;
import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.model.DashboardSnapshot;
import fr.eletutour.tavern.vaadin.model.MapLocation;
import fr.eletutour.tavern.vaadin.model.MenuBoard;
import fr.eletutour.tavern.vaadin.model.ReservationBoard;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import fr.eletutour.tavern.vaadin.model.ServiceBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTavernRepositoryTest {

    private InMemoryTavernRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTavernRepository();
    }

    @Test
    @DisplayName("fetchMapLocations renvoie les points d'intérêt connus")
    void fetchMapLocations() {
        List<MapLocation> locations = repository.fetchMapLocations();

        assertEquals(4, locations.size());
        assertTrue(locations.stream().anyMatch(l -> "The Falling Whale".equals(l.name())));
    }

    @Test
    @DisplayName("fetchRevenueData renvoie 7 jours pour un graphique en barres")
    void fetchRevenueData() {
        AnalyticsData data = repository.fetchRevenueData();

        assertNotNull(data);
        assertEquals("bar", data.chartType());
        assertEquals(7, data.labels().size());
        assertEquals(7, data.values().size());
    }

    @Test
    @DisplayName("fetchDrinkPopularity renvoie une distribution en camembert")
    void fetchDrinkPopularity() {
        AnalyticsData data = repository.fetchDrinkPopularity();

        assertEquals("pie", data.chartType());
        assertEquals(data.labels().size(), data.values().size());
    }

    @Test
    @DisplayName("fetchDashboardSnapshot compte les fûts à surveiller selon le niveau")
    void fetchDashboardSnapshotCountsLowStocks() {
        DashboardSnapshot snapshot = repository.fetchDashboardSnapshot();

        assertNotNull(snapshot);
        assertEquals(4, snapshot.metrics().size());
        assertEquals("0", snapshot.metrics().get(3).value());
    }

    @Test
    @DisplayName("fetchDashboardSnapshot signale les fûts sous le seuil de 25 pourcent")
    void fetchDashboardSnapshotFlagsCriticallyLowStocks() {
        repository.updateStock("Blonde des remparts", -42);
        repository.updateStock("Cidre du nord", -5);

        DashboardSnapshot snapshot = repository.fetchDashboardSnapshot();

        assertEquals("2", snapshot.metrics().get(3).value());
        assertTrue(snapshot.metrics().get(3).detail().contains("bientôt vides"));
    }

    @Test
    @DisplayName("fetchMenuBoard renvoie trois sections complètes")
    void fetchMenuBoard() {
        MenuBoard board = repository.fetchMenuBoard();

        assertEquals(3, board.sections().size());
        assertTrue(board.sections().stream()
                .allMatch(section -> !section.entries().isEmpty()));
    }

    @Test
    @DisplayName("fetchReservationBoard renvoie une copie défensive des réservations")
    void fetchReservationBoardIsDefensiveCopy() {
        ReservationBoard first = repository.fetchReservationBoard();
        ReservationBoard second = repository.fetchReservationBoard();

        assertNotSame(first.reservations(), second.reservations());
        assertEquals(first.reservations().size(), second.reservations().size());
    }

    @Test
    @DisplayName("addReservation ajoute une entrée au livre de salle")
    void addReservation() {
        int initial = repository.fetchReservationBoard().reservations().size();
        ReservationEntry newEntry = new ReservationEntry(
                "Compagnie des Aventuriers", 3, "22:00", "Comptoir", "Confirmée", "Test");

        repository.addReservation(newEntry);

        List<ReservationEntry> reservations = repository.fetchReservationBoard().reservations();
        assertEquals(initial + 1, reservations.size());
        assertTrue(reservations.contains(newEntry));
    }

    @Test
    @DisplayName("fetchCellarBoard renvoie une copie défensive des stocks")
    void fetchCellarBoardIsDefensiveCopy() {
        CellarBoard first = repository.fetchCellarBoard();
        CellarBoard second = repository.fetchCellarBoard();

        assertNotSame(first.stocks(), second.stocks());
    }

    @Test
    @DisplayName("updateStock applique un delta négatif sans descendre sous zéro")
    void updateStockClipsAtZero() {
        repository.updateStock("Blonde des remparts", -1000);

        CellarStock stock = findStock("Blonde des remparts");
        assertEquals(0, stock.currentLevel());
    }

    @Test
    @DisplayName("updateStock applique un delta positif sans dépasser maxLevel")
    void updateStockClipsAtMax() {
        repository.updateStock("Cidre du nord", 1000);

        CellarStock stock = findStock("Cidre du nord");
        assertEquals(stock.maxLevel(), stock.currentLevel());
    }

    @Test
    @DisplayName("updateStock applique un delta normal en mettant à jour le niveau")
    void updateStockAppliesDeltaNormally() {
        CellarStock before = findStock("Vin d'épices");

        repository.updateStock("Vin d'épices", -3);

        CellarStock after = findStock("Vin d'épices");
        assertEquals(before.currentLevel() - 3, after.currentLevel());
        assertEquals(before.maxLevel(), after.maxLevel());
        assertEquals(before.unit(), after.unit());
    }

    @Test
    @DisplayName("updateStock ignore silencieusement un produit inconnu")
    void updateStockIgnoresUnknownProduct() {
        int sizeBefore = repository.getCellarStocks().size();

        repository.updateStock("Boisson fantôme", -10);

        assertEquals(sizeBefore, repository.getCellarStocks().size());
    }

    @Test
    @DisplayName("getCellarStocks renvoie une copie défensive")
    void getCellarStocksIsDefensiveCopy() {
        List<CellarStock> first = repository.getCellarStocks();
        List<CellarStock> second = repository.getCellarStocks();

        assertNotSame(first, second);
        assertEquals(first.size(), second.size());
    }

    @Test
    @DisplayName("fetchServiceBoard renvoie les métriques et notes de service du soir")
    void fetchServiceBoard() {
        ServiceBoard board = repository.fetchServiceBoard();

        assertEquals(4, board.serviceMetrics().size());
        assertEquals(4, board.teamAssignments().size());
        assertEquals("Service du soir", board.drawerAlertTitle());
    }

    private CellarStock findStock(String name) {
        return repository.getCellarStocks().stream()
                .filter(s -> s.productName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
