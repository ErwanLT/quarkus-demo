package fr.eletutour.tavern.exception;

import fr.eletutour.tavern.locale.LocaleHelper;
import fr.eletutour.tavern.messages.TavernMessages;
import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.MessageBundles;
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
    LocaleHelper localeHelper;

    @Context
    HttpHeaders headers;

    @Override
    public Response toResponse(RateLimitException exception) {
        var locale = localeHelper.resolveLocale(headers);
        LOG.warnf("Rate limit atteint pour la locale : %s", locale);
        TavernMessages localizedMessages = resolveMessages(locale);
        return Response
                .status(429)
                .entity(localizedMessages.biereRateLimit())
                .build();
    }

    private TavernMessages resolveMessages(java.util.Locale locale) {
        try {
            return MessageBundles.get(TavernMessages.class, Localized.Literal.of(locale.toLanguageTag()));
        } catch (IllegalStateException e) {
            try {
                return MessageBundles.get(TavernMessages.class, Localized.Literal.of(locale.getLanguage()));
            } catch (IllegalStateException ignored) {
                return MessageBundles.get(TavernMessages.class);
            }
        }
    }
}
