package fr.eletutour.tavern.rest;

/**
 * Requete d'envoi d'un corbeau messager.
 *
 * @param sender  expediteur du message
 * @param content contenu du message a transmettre
 */
public record RavenDispatchRequest(String sender, String content) {
}
