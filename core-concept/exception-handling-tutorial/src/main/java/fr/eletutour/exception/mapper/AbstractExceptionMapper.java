package fr.eletutour.exception.mapper;

import fr.eletutour.exception.model.Problem;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Context;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

    @Context
    UriInfo uriInfo;

    protected abstract int status(T ex);
    protected abstract String title(T ex);

    protected String type(T ex) {
        return "about:blank";
    }

    protected String detail(T ex) {
        return ex.getMessage();
    }

    protected void enrich(Problem problem, T ex) {
        // Optionnel : enrichir avec des infos additionnelles
    }

    @Override
    public Response toResponse(T ex) {
        Problem problem = Problem.of(status(ex), title(ex))
                .type(type(ex))
                .detail(detail(ex))
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC).toString());
        if (uriInfo != null && uriInfo.getRequestUri() != null) {
            problem.instance(uriInfo.getRequestUri().toString());
        }
        enrich(problem, ex);

        return Response.status(problem.getStatus())
                .type("application/problem+json")
                .entity(problem)
                .build();
    }
}
