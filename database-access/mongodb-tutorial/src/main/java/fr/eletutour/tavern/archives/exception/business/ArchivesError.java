package fr.eletutour.tavern.archives.exception.business;

/**
 * Catalogue des erreurs métier des archives : code stable, statut, titre et
 * modèle de message. Le code alimente le {@code type} et {@code additional.code}.
 */
public enum ArchivesError {

    DOSSIER_INTROUVABLE("ARCHIVES_DOSSIER_INTROUVABLE", 404, "Dossier introuvable",
            "Aucun dossier ne porte l'identifiant %s."),
    IDENTIFIANT_INVALIDE("ARCHIVES_IDENTIFIANT_INVALIDE", 400, "Identifiant invalide",
            "L'identifiant %s n'est pas un ObjectId MongoDB valide."),
    NOM_DEJA_PRIS("ARCHIVES_NOM_DEJA_PRIS", 409, "Nom déjà pris",
            "Un dossier porte déjà ce nom dans les archives."),
    ECRITURE_IMPOSSIBLE("ARCHIVES_ECRITURE_IMPOSSIBLE", 500, "Écriture impossible",
            "MongoDB a refusé l'écriture du dossier.");

    private final String code;
    private final int status;
    private final String title;
    private final String detailTemplate;

    ArchivesError(String code, int status, String title, String detailTemplate) {
        this.code = code;
        this.status = status;
        this.title = title;
        this.detailTemplate = detailTemplate;
    }

    public String getCode() {
        return code;
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
