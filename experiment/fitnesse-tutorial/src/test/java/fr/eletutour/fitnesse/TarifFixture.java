package fr.eletutour.fitnesse;

/**
 * Règles de tarification de la taverne, exprimées sous forme de table de décision.
 * Les en-têtes de colonnes non suffixés par '?' correspondent aux setters,
 * la colonne 'prix?' à la méthode de calcul.
 */
public class TarifFixture {

    private static final int PRIX_DORTOIR = 20;
    private static final int PRIX_SUITE = 90;
    private static final int PRIX_PLACARD = 15;

    private String type;
    private int nuits;
    private boolean membre;

    public void setType(String type) {
        this.type = type;
    }

    public void setNuits(int nuits) {
        this.nuits = nuits;
    }

    public void setMembre(boolean membre) {
        this.membre = membre;
    }

    public int prix() {
        int base = switch (type == null ? "" : type) {
            case "DORTOIR" -> PRIX_DORTOIR;
            case "SUITE" -> PRIX_SUITE;
            case "PLACARD" -> PRIX_PLACARD;
            default -> throw new IllegalArgumentException("Type de chambre inconnu : " + type);
        };
        double prix = base * nuits;
        if (membre) {
            prix *= 0.8;
        }
        if (nuits >= 3) {
            prix *= 0.9;
        }
        return (int) Math.round(prix);
    }
}
