package fr.eletutour.tavern.exception;

import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {

    @Override
    public Response toResponse(RateLimitException exception) {
        return Response.status(429) // 429 Too Many Requests
                .entity("Holà l'ami ! Laisse-moi le temps de tirer ta bière, le tonneau n'est pas infini !")
                .build();
    }
}
