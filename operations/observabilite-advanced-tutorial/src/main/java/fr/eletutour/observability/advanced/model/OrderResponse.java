package fr.eletutour.observability.advanced.model;

import java.time.Instant;

public record OrderResponse(String drink, String status, Instant servedAt) {
}
