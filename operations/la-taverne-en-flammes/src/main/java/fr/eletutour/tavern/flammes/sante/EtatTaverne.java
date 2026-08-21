package fr.eletutour.tavern.flammes.sante;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Photo instantanee de l'etat de la taverne, tel que le voient les sondes de sante.
 *
 * @param cuisineEnFeu     la cuisine brule : la taverne tient debout mais ne peut plus servir (readiness DOWN)
 * @param charpenteRompue  la charpente s'est effondree : l'instance doit etre detruite (liveness DOWN)
 * @param origineIncendie  ce qui a mis le feu, pour la cause racine du post-mortem
 */
public record EtatTaverne(boolean cuisineEnFeu, boolean charpenteRompue, String origineIncendie) {

    /**
     * Ce que renvoie la sonde de liveness.
     */
    @JsonProperty("vivante")
    public boolean vivante() {
        return !charpenteRompue;
    }

    /**
     * Ce que renvoie la sonde de readiness.
     */
    @JsonProperty("prete")
    public boolean prete() {
        return !charpenteRompue && !cuisineEnFeu;
    }
}
