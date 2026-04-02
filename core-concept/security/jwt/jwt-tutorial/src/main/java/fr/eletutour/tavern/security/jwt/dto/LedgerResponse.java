package fr.eletutour.tavern.security.jwt.dto;

import java.util.List;

public record LedgerResponse(
        String book,
        List<String> entries
) {
}
