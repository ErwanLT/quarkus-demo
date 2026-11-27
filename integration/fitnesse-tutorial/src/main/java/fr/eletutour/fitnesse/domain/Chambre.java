package fr.eletutour.fitnesse.domain;

/**
 * Les chambres de la taverne : un prix par nuit et un nombre de lits limité,
 * ce qui permet aux tests FitNesse de montrer un refus quand tout est occupé.
 */
public enum Chambre {

    PLACARD("Le placard sous l'escalier", 15, 1),
    DORTOIR("Le dortoir des compagnons", 20, 6),
    SUITE("La suite du seigneur", 90, 1);

    private final String libelle;
    private final int prixParNuit;
    private final int capacite;

    Chambre(String libelle, int prixParNuit, int capacite) {
        this.libelle = libelle;
        this.prixParNuit = prixParNuit;
        this.capacite = capacite;
    }

    public String libelle() {
        return libelle;
    }

    public int prixParNuit() {
        return prixParNuit;
    }

    public int capacite() {
        return capacite;
    }

    public static Chambre depuis(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("La chambre est obligatoire");
        }
        try {
            return valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Chambre inconnue : " + code);
        }
    }
}
