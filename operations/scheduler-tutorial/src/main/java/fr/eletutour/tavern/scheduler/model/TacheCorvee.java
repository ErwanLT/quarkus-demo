package fr.eletutour.tavern.scheduler.model;

/** Les corvées que le valet de la taverne peut effectuer. */
public enum TacheCorvee {

    PRESSION(IdentifiantsCorvees.PRESSION),
    VERRES(IdentifiantsCorvees.VERRES),
    BALAYAGE(IdentifiantsCorvees.BALAYAGE),
    COMPTOIR(IdentifiantsCorvees.COMPTOIR),
    INTENDANT(IdentifiantsCorvees.INTENDANT);

    private final String identifiant;

    TacheCorvee(String identifiant) {
        this.identifiant = identifiant;
    }

    /** Même clé dans le registre et identifiant dans le scheduler. */
    public String identifiant() {
        return identifiant;
    }
}
