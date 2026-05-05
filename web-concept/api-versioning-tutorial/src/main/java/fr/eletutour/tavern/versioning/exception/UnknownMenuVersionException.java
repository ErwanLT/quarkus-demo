package fr.eletutour.tavern.versioning.exception;

public class UnknownMenuVersionException extends RuntimeException {

    private final String requestedVersion;

    public UnknownMenuVersionException(String requestedVersion) {
        super("Version de menu inconnue: " + requestedVersion);
        this.requestedVersion = requestedVersion;
    }

    public String requestedVersion() {
        return requestedVersion;
    }
}
