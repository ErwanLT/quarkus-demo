package fr.eletutour.exception.business;

public class TavernNotFoundException extends BusinessException {
    public TavernNotFoundException(String name) {
        super(404, "Ressource introuvable", "La taverne '" + name + "' n'existe pas.");
    }
}
