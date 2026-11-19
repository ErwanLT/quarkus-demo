package fr.eletutour.tavern.archives.exception.mapper;

import fr.eletutour.tavern.archives.exception.model.Problem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;

/**
 * Les erreurs de validation ({@code @Valid} sur les DTO et contraintes sur les
 * paramètres de requête) suivent exactement le même format que les erreurs métier.
 */
@Provider
public class ConstraintViolationExceptionMapper extends AbstractExceptionMapper<ConstraintViolationException> {

    @Override
    protected int status(ConstraintViolationException exception) {
        return 400;
    }

    @Override
    protected String title(ConstraintViolationException exception) {
        return "Erreur de validation des données";
    }

    @Override
    protected void enrich(Problem problem, ConstraintViolationException exception) {
        List<String> erreurs = exception.getConstraintViolations().stream()
                .map(this::formatViolation)
                .toList();
        problem.additional(Map.of("errors", erreurs));
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + " : " + violation.getMessage();
    }
}
