package fr.eletutour.tavern.raven;

import java.time.Instant;

/**
 * Message confie a un corbeau messager.
 * <p>
 * Chaque message porte l'identite de son expediteur, son contenu et l'instant
 * ou le corbeau a quitte la taverne. Une fois lache, le message est immuable :
 * on ne rattrape pas un corbeau en plein vol.
 *
 * @param sender    nom de l'expediteur du message
 * @param content   contenu du message porte par le corbeau
 * @param sentAt    instant auquel le corbeau a ete lache
 */
public record RavenMessage(String sender, String content, Instant sentAt) {

    /**
     * Cree un nouveau message pret a etre confie a un corbeau, avec l'instant
     * present comme heure de depart.
     *
     * @param sender  expediteur du message, non vide
     * @param content contenu du message, non vide
     * @return un {@link RavenMessage} pret a etre publie
     */
    public static RavenMessage from(String sender, String content) {
        return new RavenMessage(sender, content, Instant.now());
    }
}
