package fr.eletutour.exception.business;

public class TavernAlreadyExistsException extends BusinessException {
    public TavernAlreadyExistsException(String name) {
        super(409, "Conflit de données", "La taverne '" + name + "' existe déjà dans notre registre.");
    }
}
