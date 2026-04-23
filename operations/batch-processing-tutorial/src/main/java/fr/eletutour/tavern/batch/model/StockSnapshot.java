package fr.eletutour.tavern.batch.model;

import java.util.Map;

public record StockSnapshot(
    int totalStock,
    int totalConsumedToday,
    Map<BeerStyle, Integer> stockByBeer,
    Map<BeerStyle, Integer> consumedTodayByBeer
) {
}
