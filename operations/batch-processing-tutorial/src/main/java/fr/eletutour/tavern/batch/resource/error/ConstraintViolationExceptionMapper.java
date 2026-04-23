package fr.eletutour.tavern.batch.resource.error;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

@Provider
@Priority(Priorities.USER)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ConstraintViolationExceptionMapper.class);
    private static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String detail = exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining("; "));
        LOG.warnv("Constraint violation mapped to problem response: {0}", detail);

        ApiProblem problem = new ApiProblem(
            "https://tavern.eletutour.fr/problems/invalid-request",
            "Requete invalide",
            Response.Status.BAD_REQUEST.getStatusCode(),
            detail.isBlank() ? "Requete invalide" : detail,
            uriInfo == null ? "" : uriInfo.getPath()
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .type(APPLICATION_PROBLEM_JSON)
            .entity(problem)
            .build();
    }
}
