package fr.eletutour.fitnesse;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpResponse;

/**
 * Fixture de table de script : chaque ligne de la page est une phrase
 * (« le voyageur Bilbon reserve la chambre DORTOIR pour 2 nuits ») que Slim
 * transforme en appel de méthode en concaténant les cellules impaires.
 */
public class ComptoirDesReservations {

    private HttpResponse<String> derniere;

    public void leVoyageurReserveLaChambrePourNuits(String nom, String chambre, int nuits) {
        reserver(nom, chambre, nuits, false);
    }

    public void leMembreDeLaGuildeReserveLaChambrePourNuits(String nom, String chambre, int nuits) {
        reserver(nom, chambre, nuits, true);
    }

    /** Envoie un corps JSON brut : pratique pour tester un contrat d'API au plus près. */
    public void envoyerLaDemande(String json) {
        derniere = ClientTaverne.post("/taverne/reservations", json);
    }

    public void consulterLaReservation(String numero) {
        derniere = ClientTaverne.get("/taverne/reservations/" + numero);
    }

    public boolean laReservationEstAcceptee() {
        return derniere.statusCode() == 201;
    }

    public int statut() {
        return derniere.statusCode();
    }

    public String message() {
        return estJson() ? corps().get("message").asText() : derniere.body();
    }

    public int numero() {
        return corps().get("numero").asInt();
    }

    public int prix() {
        return corps().get("prix").asInt();
    }

    public String voyageur() {
        return corps().get("nom").asText();
    }

    public String adresse() {
        return derniere.headers().firstValue("Location").orElse("");
    }

    public String reponseBrute() {
        return derniere.body();
    }

    public int litsLibresEn(String chambre) {
        for (JsonNode c : ClientTaverne.json(ClientTaverne.get("/taverne/chambres").body())) {
            if (c.get("code").asText().equalsIgnoreCase(chambre)) {
                return c.get("disponibles").asInt();
            }
        }
        throw new IllegalArgumentException("Chambre inconnue : " + chambre);
    }

    private void reserver(String nom, String chambre, int nuits, boolean membre) {
        String demande = ClientTaverne.objet()
                .put("nom", nom).put("chambre", chambre).put("nuits", nuits).put("membre", membre)
                .toString();
        derniere = ClientTaverne.post("/taverne/reservations", demande);
    }

    private boolean estJson() {
        return derniere.headers().firstValue("Content-Type").orElse("").startsWith("application/json");
    }

    private JsonNode corps() {
        if (!estJson()) {
            throw new IllegalStateException("Pas de réservation : HTTP " + derniere.statusCode()
                    + " « " + derniere.body() + " »");
        }
        return ClientTaverne.json(derniere.body());
    }
}
