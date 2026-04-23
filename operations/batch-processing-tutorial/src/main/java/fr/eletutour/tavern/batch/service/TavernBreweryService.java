package fr.eletutour.tavern.batch.service;

import fr.eletutour.tavern.batch.model.BeerStyle;
import fr.eletutour.tavern.batch.model.DayReport;
import fr.eletutour.tavern.batch.model.StockSnapshot;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class TavernBreweryService {

    private static final int INITIAL_STOCK_PER_BEER = 120;

    private final EnumMap<BeerStyle, Integer> stockByStyle = new EnumMap<>(BeerStyle.class);
    private final EnumMap<BeerStyle, Integer> consumedTodayByStyle = new EnumMap<>(BeerStyle.class);
    private EnumMap<BeerStyle, Integer> lastBrewedByStyle = new EnumMap<>(BeerStyle.class);

    @PostConstruct
    void init() {
        resetSimulation();
    }

    /**
     * Consomme les bieres de la journee selon les demandes recues.
     *
     * @param requestedOrders demandes agregees par type de biere, quantites >= 0
     * @return rapport de service et de rupture pour la journee
     */
    public synchronized DayReport consumeDuringDay(Map<BeerStyle, Integer> requestedOrders) {
        EnumMap<BeerStyle, Integer> requestedByStyle = aggregateRequestedByStyle(requestedOrders);
        EnumMap<BeerStyle, Integer> servedByStyle = new EnumMap<>(BeerStyle.class);
        EnumMap<BeerStyle, Integer> missingByStyle = new EnumMap<>(BeerStyle.class);

        int totalRequested = 0;
        int totalServed = 0;
        int totalMissing = 0;

        for (BeerStyle style : BeerStyle.values()) {
            int requested = requestedByStyle.get(style);
            int available = stockByStyle.get(style);
            int served = Math.min(requested, available);
            int missing = requested - served;

            stockByStyle.put(style, available - served);
            consumedTodayByStyle.put(style, consumedTodayByStyle.get(style) + served);

            servedByStyle.put(style, served);
            missingByStyle.put(style, missing);

            totalRequested += requested;
            totalServed += served;
            totalMissing += missing;
        }

        return new DayReport(
            totalRequested,
            totalServed,
            totalMissing,
            toOrderedMap(servedByStyle),
            toOrderedMap(missingByStyle)
        );
    }

    /**
     * Brasse la nuit exactement le volume servi pendant la journee.
     *
     * @return volume total brasse pendant le lot nocturne
     */
    public synchronized int brewConsumedBeers() {
        EnumMap<BeerStyle, Integer> brewedByStyle = new EnumMap<>(BeerStyle.class);
        int totalBrewed = 0;

        for (BeerStyle style : BeerStyle.values()) {
            int consumedToday = consumedTodayByStyle.get(style);
            int brewedTonight = consumedToday;

            stockByStyle.put(style, stockByStyle.get(style) + brewedTonight);
            consumedTodayByStyle.put(style, 0);

            brewedByStyle.put(style, brewedTonight);
            totalBrewed += brewedTonight;
        }

        lastBrewedByStyle = brewedByStyle;
        return totalBrewed;
    }

    public synchronized Map<BeerStyle, Integer> getLastBrewedByStyle() {
        return Collections.unmodifiableMap(toOrderedMap(lastBrewedByStyle));
    }

    public synchronized StockSnapshot currentStock() {
        int totalStock = 0;
        int totalConsumedToday = 0;

        for (BeerStyle style : BeerStyle.values()) {
            totalStock += stockByStyle.get(style);
            totalConsumedToday += consumedTodayByStyle.get(style);
        }

        return new StockSnapshot(
            totalStock,
            totalConsumedToday,
            toOrderedMap(stockByStyle),
            toOrderedMap(consumedTodayByStyle)
        );
    }

    public synchronized void resetSimulation() {
        for (BeerStyle style : BeerStyle.values()) {
            stockByStyle.put(style, INITIAL_STOCK_PER_BEER);
            consumedTodayByStyle.put(style, 0);
        }
        lastBrewedByStyle = new EnumMap<>(BeerStyle.class);
        for (BeerStyle style : BeerStyle.values()) {
            lastBrewedByStyle.put(style, 0);
        }
    }

    private EnumMap<BeerStyle, Integer> aggregateRequestedByStyle(Map<BeerStyle, Integer> requestedOrders) {
        EnumMap<BeerStyle, Integer> requestedByStyle = new EnumMap<>(BeerStyle.class);
        for (BeerStyle style : BeerStyle.values()) {
            requestedByStyle.put(style, 0);
        }

        if (requestedOrders == null) {
            return requestedByStyle;
        }

        for (Map.Entry<BeerStyle, Integer> entry : requestedOrders.entrySet()) {
            BeerStyle style = entry.getKey();
            if (style == null) {
                continue;
            }
            int sanitizedQuantity = Math.max(entry.getValue() == null ? 0 : entry.getValue(), 0);
            requestedByStyle.merge(style, sanitizedQuantity, Integer::sum);
        }

        return requestedByStyle;
    }

    private Map<BeerStyle, Integer> toOrderedMap(Map<BeerStyle, Integer> source) {
        Map<BeerStyle, Integer> ordered = new LinkedHashMap<>();
        for (BeerStyle style : BeerStyle.values()) {
            ordered.put(style, source.getOrDefault(style, 0));
        }
        return ordered;
    }
}
