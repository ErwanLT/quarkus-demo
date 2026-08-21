package fr.eletutour.tavern.flammes.arret;

import java.time.Duration;
import java.time.Instant;

/**
 * Une tournee servie jusqu'au bout, meme si le signal d'arret est arrive entre-temps.
 */
public record TourneeServie(String aventurier, long dureeServiceMs, int tourneesEncoreEnCours, Instant servieLe) {

    public static TourneeServie de(String aventurier, Duration duree, int tourneesEncoreEnCours) {
        return new TourneeServie(aventurier, duree.toMillis(), tourneesEncoreEnCours, Instant.now());
    }
}
