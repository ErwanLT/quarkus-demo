package fr.eletutour.validation.constraint;

import fr.eletutour.validation.model.ReservationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.ChronoUnit;

public class SejourCoherentValidator implements ConstraintValidator<SejourCoherent, ReservationRequest> {

    @Override
    public boolean isValid(ReservationRequest request, ConstraintValidatorContext context) {
        // Les dates absentes sont déjà signalées par @NotNull : on ne doublonne pas l'erreur.
        if (request == null || request.arrivee() == null || request.depart() == null) {
            return true;
        }

        long nuits = ChronoUnit.DAYS.between(request.arrivee(), request.depart());
        if (nuits >= 1 && nuits <= 7) {
            return true;
        }

        // Rattacher la violation au champ « depart » pour que le mapper puisse
        // renvoyer un champ utile au client, et pas seulement le nom du bean.
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("depart")
                .addConstraintViolation();
        return false;
    }
}
