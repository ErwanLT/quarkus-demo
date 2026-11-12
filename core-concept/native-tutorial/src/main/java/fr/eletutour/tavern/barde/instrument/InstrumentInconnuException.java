package fr.eletutour.tavern.barde.instrument;

public class InstrumentInconnuException extends RuntimeException {

    public InstrumentInconnuException(String nom) {
        super("Le luthier ne sait pas fabriquer : " + nom);
    }

    public InstrumentInconnuException(String nom, Throwable cause) {
        super("Le luthier ne sait pas fabriquer : " + nom + " (" + cause.getClass().getSimpleName() + ")", cause);
    }
}
