package fr.eletutour.fitnesse;

/**
 * Table de décision : chaque ligne renseigne {@code nom}, puis Slim lit
 * {@code message d'accueil?} — qui devient un appel HTTP à l'endpoint.
 */
public class AccueilDesVoyageurs {

    private String nom;

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String messageDAccueil() {
        var reponse = ClientTaverne.get("/taverne/accueil?nom=" + ClientTaverne.encoder(nom));
        if (reponse.statusCode() != 200) {
            throw new IllegalStateException("HTTP " + reponse.statusCode() + " : " + reponse.body());
        }
        return reponse.body();
    }
}
