package fr.eletutour.tavern.flammes.grimoire;

/**
 * Une recette tiree du grimoire.
 *
 * @param nom       le nom du plat
 * @param texte     la recette elle-meme
 * @param origine   {@code GRIMOIRE} si le grimoire distant a repondu, {@code MEMOIRE_DU_CHEF} sinon
 * @param tentatives nombre d'appels reellement passes au grimoire distant
 */
public record Recette(String nom, String texte, String origine, int tentatives) {

    public static final String ORIGINE_GRIMOIRE = "GRIMOIRE";
    public static final String ORIGINE_SECOURS = "MEMOIRE_DU_CHEF";
}
