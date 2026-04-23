package fr.eletutour.tavern.batch.model;

import java.util.Map;

public record DayReport(
    int totalRequested,
    int totalServed,
    int totalMissing,
    Map<BeerStyle, Integer> servedByBeer,
    Map<BeerStyle, Integer> missingByBeer
) {
}
