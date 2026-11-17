package fr.eletutour.tavern.coursier.exception.business;

/**
 * Le registre des mésaventures du coursier : un code stable, un statut HTTP,
 * un titre et un modèle de détail.
 */
public enum CoursierError {

    CONTRAT_INTROUVABLE(404, "Contrat introuvable",
            "La Guilde ne connaît pas ce contrat : il a peut-être déjà été retiré du tableau."),
    RECETTE_INTROUVABLE(404, "Recette introuvable",
            "Le Grimoire est muet sur cette recette."),
    DIFFICULTE_INCONNUE(400, "Difficulté inconnue",
            "La difficulté '%s' n'existe pas. Valeurs possibles : %s."),
    REPONSE_INATTENDUE(502, "Réponse inattendue d'un service distant",
            "%s a répondu %d au coursier, qui revient les mains vides."),
    SERVICE_INJOIGNABLE(502, "Service distant injoignable",
            "Un service consulté par le coursier ne répond pas. Réessayez plus tard.");

    private final int status;
    private final String title;
    private final String detailTemplate;

    CoursierError(int status, String title, String detailTemplate) {
        this.status = status;
        this.title = title;
        this.detailTemplate = detailTemplate;
    }

    public String getCode() {
        return name();
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String formatDetail(Object... args) {
        return String.format(detailTemplate, args);
    }
}
