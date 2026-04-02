package fr.eletutour.tavern.security.jwt.jpa.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresAt
) {
}
