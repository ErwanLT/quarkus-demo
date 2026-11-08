package fr.eletutour.tavern.editique.model;

/**
 * Une carte de table : son nom et l'URL encodée dans le QR code.
 */
public class CarteTable {

    private final String nom;
    private final String url;

    public CarteTable(String nom, String url) {
        this.nom = nom;
        this.url = url;
    }

    public String getNom() {
        return nom;
    }

    public String getUrl() {
        return url;
    }
}
