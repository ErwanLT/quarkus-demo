package fr.eletutour.tavern.archives.exception.mapper;

import fr.eletutour.tavern.archives.exception.model.Problem;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Construction de la réponse d'erreur centralisée : chaque mapper ne fournit
 * que le statut, le titre et, au besoin, un type, un détail ou un enrichissement.
 */
public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

    @Context
    UriInfo uriInfo;

    protected abstract int status(T exception);

    protected abstract String title(T exception);

    protected String type(T exception) {
        return "about:blank";
    }

    protected String detail(T exception) {
        return exception.getMessage();
    }

    protected void enrich(Problem problem, T exception) {
        // Les mappers spécialisés ajoutent leurs informations ici.
    }

    @Override
    public Response toResponse(T exception) {
        Problem problem = Problem.of(status(exception), title(exception))
                .type(type(exception))
                .detail(detail(exception))
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC).toString());
        if (uriInfo != null && uriInfo.getRequestUri() != null) {
            problem.instance(uriInfo.getRequestUri().toString());
        }
        enrich(problem, exception);

        return Response.status(problem.getStatus())
                .type("application/problem+json")
                .entity(problem)
                .build();
    }
}
