package fr.eletutour.tavern.security.jwt.dto;

import java.util.List;

public record VaultResponse(
        String vault,
        String status,
        List<String> assets
) {
}
