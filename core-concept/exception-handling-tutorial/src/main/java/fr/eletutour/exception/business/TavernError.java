package fr.eletutour.exception.business;

public enum TavernError {
    NOT_FOUND("TAVERN_NOT_FOUND", 404, "Ressource introuvable", "La taverne '%s' n'existe pas."),
    ALREADY_EXISTS("TAVERN_ALREADY_EXISTS", 409, "Conflit de données", "La taverne '%s' existe déjà dans notre registre."),
    CAPACITY_REACHED("TAVERN_CAPACITY_REACHED", 422, "Capacité atteinte", "Impossible d'accueillir plus de monde à %s");

    private final String code;
    private final int status;
    private final String title;
    private final String detailTemplate;

    TavernError(String code, int status, String title, String detailTemplate) {
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
