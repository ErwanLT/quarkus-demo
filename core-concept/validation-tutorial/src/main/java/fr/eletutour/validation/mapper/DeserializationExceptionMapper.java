package fr.eletutour.validation.mapper;

import fr.eletutour.validation.model.Problem;
import fr.eletutour.validation.model.ValeurChambreInconnueException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;

/**
 * Quand Jackson ne peut pas construire une enum, RESTEasy enveloppe la cause
 * dans un WebApplicationException 400 : un mapper déclaré sur
 * ValeurChambreInconnueException ne serait donc jamais appelé, d'où ce type-ci.
 *
 * Le mapper ne spécialise que les erreurs dont la cause est une
 * ValeurChambreInconnueException. Pour toute autre WebApplicationException, il
 * produit exactement la même réponse que GlobalExceptionMapper (statut et
 * message de l'exception, sans enrichissement) : aucun comportement n'est
 * détourné, le périmètre est une contrainte de RESTEasy.
 */
@Provider
public class DeserializationExceptionMapper extends AbstractExceptionMapper<WebApplicationException> {

    @Override
    protected int status(WebApplicationException ex) {
        return ex.getResponse().getStatus();
    }

    @Override
    protected String title(WebApplicationException ex) {
        return cause(ex) == null ? "Erreur lors du traitement de la requête" : "Erreur de validation des données";
    }

    @Override
    protected String detail(WebApplicationException ex) {
        ValeurChambreInconnueException cause = cause(ex);
        return cause == null ? ex.getMessage() : cause.getMessage();
    }

    @Override
    protected void enrich(Problem problem, WebApplicationException ex) {
        ValeurChambreInconnueException cause = cause(ex);
        if (cause != null) {
            problem.additional(Map.of(
                    "errors", List.of(Map.of(
                            "field", cause.getField(),
                            "message", cause.getMessage(),
                            "code", "TypeChambreInconnu")),
                    "violations", 1));
        }
    }

    private ValeurChambreInconnueException cause(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof ValeurChambreInconnueException cause) {
                return cause;
            }
            current = current.getCause();
        }
        return null;
    }
}
