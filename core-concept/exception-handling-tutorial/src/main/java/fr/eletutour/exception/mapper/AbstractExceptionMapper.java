package fr.eletutour.exception.mapper;

import fr.eletutour.exception.model.Problem;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

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
                .detail(detail(ex));
        enrich(problem, ex);

        return Response.status(problem.getStatus())
                .type("application/problem+json")
                .entity(problem)
                .build();
    }
}
