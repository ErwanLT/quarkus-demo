package fr.eletutour.tavern.exception;

import fr.eletutour.tavern.locale.LocaleHelper;
import fr.eletutour.tavern.messages.TavernMessages;
import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Mapper JAX-RS pour les exceptions de rate limiting.
 * <p>
 * Traduit une {@link RateLimitException} en HTTP 429 avec un message
 * localisé dans la langue du client. Sans ce mapper, Quarkus retournerait
 * un 500 générique sans personnalité ni traduction.
 * </p>
 *
 * <p>L'annotation {@code @Provider} enregistre automatiquement ce mapper
 * dans le pipeline JAX-RS — aucune configuration supplémentaire requise.</p>
 */
@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {

    private static final Logger LOG = Logger.getLogger(RateLimitExceptionMapper.class);

    @Inject
    TavernMessages messages;

    @Inject
    LocaleHelper localeHelper;

    @Context
    HttpHeaders headers;

    @Override
    public Response toResponse(RateLimitException exception) {
        LOG.warnf("Rate limit atteint pour la locale : %s", localeHelper.resolveLocale(headers));
        return Response
                .status(429)
                .entity(messages.biereRateLimit())
                .build();
    }
}
