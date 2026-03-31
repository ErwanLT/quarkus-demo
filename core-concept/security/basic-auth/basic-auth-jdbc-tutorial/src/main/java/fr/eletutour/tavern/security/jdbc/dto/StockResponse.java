package fr.eletutour.tavern.security.jdbc.dto;

import java.util.List;

public record StockResponse(
        String area,
        List<String> items
) {
}
