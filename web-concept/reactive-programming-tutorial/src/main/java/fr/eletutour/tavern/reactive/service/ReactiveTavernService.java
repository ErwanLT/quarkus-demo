package fr.eletutour.tavern.reactive.service;

import fr.eletutour.tavern.reactive.dto.BeerResponse;
import fr.eletutour.tavern.reactive.dto.ClientOrderResponse;
import fr.eletutour.tavern.reactive.dto.ProgrammingStyleResponse;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class ReactiveTavernService {

    private static final long DEFAULT_DELAY_MS = 300;
    private static final long MIN_DELAY_MS = 10;
    private static final long MAX_DELAY_MS = 2_000;
    private static final int DEFAULT_CLIENT_COUNT = 5;
    private static final int MAX_CLIENT_COUNT = 20;
    private static final List<String> ORDERS = List.of(
            "Biere ambrée du Dragon Dormant",
            "Hydromel des collines",
            "Cidre de la Licorne",
            "Infusion de sauge pour mage fatigue",
            "Potion de Panacee Interstellaire (ne pas boire avant un combat)"
    );
    private static final List<String> DEFAULT_TOURNEE = List.of("Gimli", "Legolas", "Frodon");

    public Uni<BeerResponse> pourBeer(String adventurer, long durationMs) {
        String safeAdventurer = normalizeAdventurer(adventurer);
        long safeDurationMs = sanitizeDelay(durationMs);

        return Uni.createFrom()
                .item(() -> new BeerResponse(
                        safeAdventurer,
                        ORDERS.getFirst(),
                        safeDurationMs,
                        "Le tavernier lance la pression, sert une table, puis revient quand la chope est pleine."
                ))
                .onItem()
                .delayIt()
                .by(Duration.ofMillis(safeDurationMs));
    }

    public Multi<ClientOrderResponse> streamClientOrders(int count, long intervalMs) {
        int safeCount = sanitizeCount(count);
        long safeIntervalMs = sanitizeDelay(intervalMs);

        return Multi.createFrom()
                .ticks()
                .every(Duration.ofMillis(safeIntervalMs))
                .select()
                .first(safeCount)
                .onItem()
                .transform(sequence -> createClientOrder(sequence + 1))
                .onCancellation()
                .invoke(() -> Log.info(
                        "Un client quitte la taverne avant la fin de la tournee, le tavernier passe a autre chose sans se figer."
                ));
    }

    /**
     * Sert plusieurs aventuriers en meme temps pour prouver que le tavernier aux huit bras
     * ne bloque jamais un bras pour un seul client : le temps total observe se rapproche du
     * plus long des services, pas de leur somme.
     */
    public Uni<List<BeerResponse>> pourTournee(List<String> adventurers, long durationMs) {
        List<String> safeAdventurers = sanitizeAdventurers(adventurers);

        List<Uni<BeerResponse>> pours = safeAdventurers.stream()
                .map(adventurer -> pourBeer(adventurer, durationMs))
                .toList();

        return Uni.join().all(pours).andFailFast();
    }

    public Uni<List<ClientOrderResponse>> collectClientOrders(int count, long intervalMs) {
        return streamClientOrders(count, intervalMs)
                .collect()
                .asList();
    }

    public ProgrammingStyleResponse compareStyles() {
        return new ProgrammingStyleResponse(
                "Imperatif : le tavernier reste devant le fut jusqu'a ce que la biere soit servie. Le thread attend.",
                "Reactif : le tavernier declare ce qui arrivera quand la biere sera prete. Le thread peut servir ailleurs.",
                "Uni : une seule chope promise, livree une fois quand elle est pleine.",
                "Multi : une file de clients, recue commande apres commande tant que le comptoir vit."
        );
    }

    private ClientOrderResponse createClientOrder(long sequence) {
        String order = ORDERS.get((int) ((sequence - 1) % ORDERS.size()));
        return new ClientOrderResponse(
                sequence,
                "Client " + sequence,
                order,
                "Le tavernier capte la commande " + sequence + " sans immobiliser ses huit bras."
        );
    }

    private String normalizeAdventurer(String adventurer) {
        if (adventurer == null || adventurer.isBlank()) {
            return "Aventurier anonyme";
        }
        return adventurer.trim();
    }

    private long sanitizeDelay(long delayMs) {
        if (delayMs <= 0) {
            return DEFAULT_DELAY_MS;
        }
        return Math.clamp(delayMs, MIN_DELAY_MS, MAX_DELAY_MS);
    }

    private int sanitizeCount(int count) {
        if (count <= 0) {
            return DEFAULT_CLIENT_COUNT;
        }
        return Math.min(count, MAX_CLIENT_COUNT);
    }

    private List<String> sanitizeAdventurers(List<String> adventurers) {
        if (adventurers == null || adventurers.isEmpty()) {
            return DEFAULT_TOURNEE;
        }
        List<String> cleaned = adventurers.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .toList();
        return cleaned.isEmpty() ? DEFAULT_TOURNEE : cleaned;
    }
}