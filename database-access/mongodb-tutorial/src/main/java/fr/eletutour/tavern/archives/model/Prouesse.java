package fr.eletutour.tavern.archives.model;

/**
 * Un haut fait, stocké <strong>dans</strong> le dossier de l'aventurier.
 *
 * <p>C'est un simple POJO : le codec MongoDB du driver le sérialise en
 * sous-document. Pas d'entité Panache, pas de collection séparée.</p>
 */
public class Prouesse {

    public String titre;
    public Rang rang;
    public int annee;
    public int recompense;

    public Prouesse() {
        // requis par le codec POJO de MongoDB
    }

    public Prouesse(String titre, Rang rang, int annee, int recompense) {
        this.titre = titre;
        this.rang = rang;
        this.annee = annee;
        this.recompense = recompense;
    }
}
