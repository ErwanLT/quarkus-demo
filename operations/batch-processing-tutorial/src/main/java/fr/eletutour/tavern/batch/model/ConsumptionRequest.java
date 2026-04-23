package fr.eletutour.tavern.batch.model;

import java.util.Map;

public record ConsumptionRequest(Map<BeerStyle, Integer> orders) {
}
