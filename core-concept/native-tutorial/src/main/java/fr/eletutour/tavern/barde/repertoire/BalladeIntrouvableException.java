package fr.eletutour.tavern.barde.repertoire;

public class BalladeIntrouvableException extends RuntimeException {

    public BalladeIntrouvableException(String chemin) {
        super("Parchemin introuvable : " + chemin);
    }
}
