package fr.eletutour.tavern.rest;

import java.time.Instant;

/**
 * Confirmation qu'un corbeau a quitte la taverne avec son message.
 *
 * @param sender  expediteur du message
 * @param content contenu transmis au corbeau
 * @param sentAt  instant du depart du corbeau
 */
public record RavenDispatchResponse(String sender, String content, Instant sentAt) {
}
