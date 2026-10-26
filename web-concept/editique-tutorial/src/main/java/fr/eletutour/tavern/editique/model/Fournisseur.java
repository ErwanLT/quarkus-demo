package fr.eletutour.tavern.editique.model;

/**
 * Un fournisseur de la taverne (brasseur nain, meunier, fromager hobbit...).
 */
public class Fournisseur {

    private final String code;
    private final String nom;
    private final String adresse;
    private final String ville;

    public Fournisseur(String code, String nom, String adresse, String ville) {
        this.code = code;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getVille() {
        return ville;
    }
}
