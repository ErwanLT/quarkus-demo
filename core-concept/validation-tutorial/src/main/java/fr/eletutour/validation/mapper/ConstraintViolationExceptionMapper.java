package fr.eletutour.validation.mapper;

import fr.eletutour.validation.model.Problem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.ext.Provider;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Le mapper de validation du tutoriel exception-handling, enrichi pour rendre
 * exploitables les violations de contraintes standard et custom : champ, message
 * et nom de l'annotation violée.
 */
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
    protected String detail(ConstraintViolationException ex) {
        int count = ex.getConstraintViolations().size();
        return count + " règle(s) de validation violée(s).";
    }

    @Override
    protected void enrich(Problem problem, ConstraintViolationException ex) {
        List<Map<String, Object>> errors = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .sorted(Comparator.comparing(error -> (String) error.get("field")))
                .toList();

        problem.additional(Map.of("errors", errors, "violations", errors.size()));
    }

    private Map<String, Object> formatViolation(ConstraintViolation<?> violation) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("field", fieldOf(violation));
        error.put("message", violation.getMessage());
        error.put("code", violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
        return error;
    }

    private String fieldOf(ConstraintViolation<?> violation) {
        // RESTEasy préfixe le chemin par la méthode et son paramètre
        // (« reserver.request.chambre ») : on ne garde que le dernier nœud.
        Path path = violation.getPropertyPath();
        String field = null;
        if (path != null) {
            for (Path.Node node : path) {
                field = node.getName();
            }
        }
        return field == null || field.isBlank() ? "reservation" : field;
    }
}
