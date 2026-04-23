package fr.eletutour.tavern.batch.resource.error;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(RuntimeException exception) {
        return switch (exception) {
            case BadRequestException badRequestException -> toProblemResponse(
                Response.Status.BAD_REQUEST,
                "https://tavern.eletutour.fr/problems/invalid-request",
                "Requete invalide",
                badRequestException.getMessage()
            );
            case IllegalArgumentException illegalArgumentException -> toProblemResponse(
                Response.Status.BAD_REQUEST,
                "https://tavern.eletutour.fr/problems/invalid-request",
                "Requete invalide",
                illegalArgumentException.getMessage()
            );
            case IllegalStateException illegalStateException -> toProblemResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "https://tavern.eletutour.fr/problems/internal-error",
                "Erreur interne",
                illegalStateException.getMessage()
            );
            case InternalServerErrorException internalServerErrorException -> toProblemResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "https://tavern.eletutour.fr/problems/internal-error",
                "Erreur interne",
                internalServerErrorException.getMessage()
            );
            default -> toProblemResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "https://tavern.eletutour.fr/problems/unexpected-error",
                "Erreur inattendue",
                "Une erreur inattendue est survenue"
            );
        };
    }

    private Response toProblemResponse(Response.Status status, String type, String title, String detail) {
        ApiProblem problem = new ApiProblem(
            type,
            title,
            status.getStatusCode(),
            detail == null ? "Aucun detail" : detail,
            uriInfo == null ? "" : uriInfo.getPath()
        );

        return Response.status(status)
            .type(APPLICATION_PROBLEM_JSON)
            .entity(problem)
            .build();
    }
}
