package fr.eletutour.validation.business;

import fr.eletutour.validation.model.TypeChambre;

import java.time.LocalDate;

public class TavernException extends BusinessException {
    private final TavernError error;

    public TavernException(TavernError error, Object... detailArgs) {
        super(error.getStatus(), error.getTitle(), error.formatDetail(detailArgs));
        this.error = error;
    }

    public static TavernException chambreDejaReservee(TypeChambre chambre, LocalDate arrivee) {
        return new TavernException(TavernError.CHAMBRE_OCCUPEE, chambre.valeur(), arrivee);
    }

    public TavernError getError() { return error; }
}
