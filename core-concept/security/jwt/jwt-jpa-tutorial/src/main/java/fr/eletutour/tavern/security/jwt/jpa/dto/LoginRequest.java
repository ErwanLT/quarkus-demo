package fr.eletutour.tavern.security.jwt.jpa.dto;

public record LoginRequest(
        String username,
        String password
) {
}
