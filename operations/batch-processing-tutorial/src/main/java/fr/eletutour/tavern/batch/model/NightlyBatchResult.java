package fr.eletutour.tavern.batch.model;

import java.util.Map;

public record NightlyBatchResult(
    long executionId,
    String jobName,
    String batchStatus,
    int totalBrewed,
    Map<BeerStyle, Integer> brewedByBeer
) {
}
