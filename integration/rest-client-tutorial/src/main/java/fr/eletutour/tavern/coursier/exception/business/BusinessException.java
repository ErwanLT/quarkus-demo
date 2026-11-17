package fr.eletutour.tavern.coursier.exception.business;

/**
 * Socle des erreurs métier : chacune connaît déjà son statut HTTP et son titre,
 * le mapper n'a plus qu'à les mettre en forme.
 */
public abstract class BusinessException extends RuntimeException {

    private final int status;
    private final String title;

    protected BusinessException(int status, String title, String message) {
        super(message);
        this.status = status;
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
