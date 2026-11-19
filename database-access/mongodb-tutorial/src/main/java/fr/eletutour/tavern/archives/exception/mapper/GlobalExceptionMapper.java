package fr.eletutour.tavern.archives.exception.mapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

/**
 * Filet de sécurité : une erreur inattendue ne doit jamais exposer son message
 * technique au client. Les exceptions JAX-RS gardent leur statut (400, 404, ...).
 */
@Provider
public class GlobalExceptionMapper extends AbstractExceptionMapper<Throwable> {

    @Override
    protected int status(Throwable exception) {
        if (exception instanceof WebApplicationException web) {
            return web.getResponse().getStatus();
        }
        return 500;
    }

    @Override
    protected String title(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            return "Erreur lors du traitement de la requête";
        }
        return "Erreur interne du serveur";
    }

    @Override
    protected String detail(Throwable exception) {
        if (exception instanceof WebApplicationException web) {
            return web.getMessage();
        }
        return "Une erreur inattendue est survenue lors du traitement de votre requête.";
    }
}
