package fr.eletutour.tavern.exception;

import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mappeur d'exception pour gérer globalement les excès de requêtes.
 * <p>
 * Intercepte les exceptions de type {@link RateLimitException} jetées par
 * l'annotation @RateLimit de SmallRye Fault Tolerance. Convertit ces erreurs internes
 * en une réponse HTTP 429 signifiant "Too Many Requests".
 * </p>
 */
@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {

    /**
     * Transforme l'exception {@link RateLimitException} en réponse HTTP pour le client.
     *
     * @param exception l'exception interne capturée
     * @return une {@link Response} formatée avec le code HTTP 429 et un message de modération
     */
    @Override
    public Response toResponse(RateLimitException exception) {
        return Response.status(429) // 429 Too Many Requests
                .entity("Holà l'ami ! Laisse-moi le temps de tirer ta bière, le tonneau n'est pas infini !")
                .build();
    }
}
