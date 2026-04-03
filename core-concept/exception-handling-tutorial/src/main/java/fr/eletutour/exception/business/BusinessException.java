package fr.eletutour.exception.business;

public abstract class BusinessException extends RuntimeException {
    private final int status;
    private final String title;

    protected BusinessException(int status, String title, String message) {
        super(message);
        this.status = status;
        this.title = title;
    }

    public int getStatus() { return status; }
    public String getTitle() { return title; }
}
