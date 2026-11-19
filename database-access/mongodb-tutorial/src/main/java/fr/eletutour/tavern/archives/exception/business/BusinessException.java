package fr.eletutour.tavern.archives.exception.business;

/**
 * Une erreur métier porte elle-même son statut HTTP et son titre : les mappers
 * n'ont pas à connaître chaque cas métier pour construire la réponse.
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
