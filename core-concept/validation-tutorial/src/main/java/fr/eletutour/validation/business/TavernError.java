package fr.eletutour.validation.business;

public enum TavernError {
    CHAMBRE_OCCUPEE("CHAMBRE_DEJA_RESERVEE", 409, "Chambre déjà réservée",
            "La chambre %s est déjà réservée pour le %s.");

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

    public String getCode() { return code; }
    public int getStatus() { return status; }
    public String getTitle() { return title; }
    public String formatDetail(Object... args) { return String.format(detailTemplate, args); }
}
