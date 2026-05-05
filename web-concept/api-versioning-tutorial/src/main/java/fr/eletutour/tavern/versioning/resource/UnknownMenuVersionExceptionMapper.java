package fr.eletutour.tavern.versioning.resource;

import fr.eletutour.tavern.versioning.dto.ProblemDetailsResponse;
import fr.eletutour.tavern.versioning.exception.UnknownMenuVersionException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnknownMenuVersionExceptionMapper implements ExceptionMapper<UnknownMenuVersionException> {

    public static final String PROBLEM_JSON = "application/problem+json";

    @Override
    public Response toResponse(UnknownMenuVersionException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(PROBLEM_JSON)
                .entity(new ProblemDetailsResponse(
                        "https://eletutour.fr/problems/api-version-unknown",
                        "Version d'API inconnue",
                        Response.Status.BAD_REQUEST.getStatusCode(),
                        "Le grimoire d'API ne connait que les versions 1 et 2.",
                        "VERSION_INCONNUE"
                ))
                .build();
    }
}
