package fr.eletutour.tavern.archives.dto;

import jakarta.ws.rs.core.Response;

/**
 * Erreur au format RFC 9457.
 */
public record Probleme(String type, String title, int status, String detail) {

    public static Response reponse(int status, String title, String detail) {
        return Response.status(status)
                .type("application/problem+json")
                .entity(new Probleme("about:blank", title, status, detail))
                .build();
    }
}
