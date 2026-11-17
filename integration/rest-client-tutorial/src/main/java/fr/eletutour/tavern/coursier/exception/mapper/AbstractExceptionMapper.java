package fr.eletutour.tavern.coursier.exception.mapper;

import fr.eletutour.tavern.coursier.exception.model.Probleme;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * Gabarit commun : chaque mapper fournit le statut, le titre et éventuellement
 * le type, le détail et les informations complémentaires ; la mise en forme
 * RFC 9457 est faite une seule fois, ici.
 */
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

    protected Map<String, Object> additional(T ex) {
        return null;
    }

    @Override
    public Response toResponse(T ex) {
        String instance = uriInfo != null && uriInfo.getRequestUri() != null
                ? uriInfo.getRequestUri().toString()
                : null;
        return new Probleme(
                type(ex),
                title(ex),
                status(ex),
                detail(ex),
                instance,
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                additional(ex))
                .enReponse();
    }
}
