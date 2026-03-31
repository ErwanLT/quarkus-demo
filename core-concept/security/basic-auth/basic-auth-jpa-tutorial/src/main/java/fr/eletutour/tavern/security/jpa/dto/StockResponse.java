package fr.eletutour.tavern.security.jpa.dto;

import java.util.List;

public record StockResponse(
        String area,
        List<String> items
) {
}
