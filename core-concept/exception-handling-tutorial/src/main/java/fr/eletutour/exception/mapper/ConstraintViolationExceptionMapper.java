package fr.eletutour.exception.mapper;

import fr.eletutour.exception.model.Problem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper extends AbstractExceptionMapper<ConstraintViolationException> {

    @Override
    protected int status(ConstraintViolationException ex) {
        return 400;
    }

    @Override
    protected String title(ConstraintViolationException ex) {
        return "Erreur de validation des données";
    }

    @Override
    protected void enrich(Problem problem, ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.toList());

        problem.additional(Map.of("errors", errors));
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        return String.format("%s : %s", violation.getPropertyPath(), violation.getMessage());
    }
}
