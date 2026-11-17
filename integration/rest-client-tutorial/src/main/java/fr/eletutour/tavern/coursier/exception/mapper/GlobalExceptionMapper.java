package fr.eletutour.tavern.coursier.exception.mapper;

import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Filet de sécurité : tout ce qui n'a pas de mapper dédié sort quand même en
 * {@code problem+json}.
 */
@Provider
public class GlobalExceptionMapper extends AbstractExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        // Une WebApplicationException qui porte déjà son corps (les services
        // simulés en lèvent) est une réponse finie : on la laisse passer.
        if (ex instanceof WebApplicationException we && we.getResponse().hasEntity()) {
            return we.getResponse();
        }
        if (!(ex instanceof WebApplicationException)) {
            Log.error("Erreur inattendue dans la taverne", ex);
        }
        return super.toResponse(ex);
    }

    @Override
    protected int status(Throwable ex) {
        if (ex instanceof WebApplicationException we) {
            return we.getResponse().getStatus();
        }
        return 500;
    }

    @Override
    protected String title(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            return "Erreur lors du traitement de la requête";
        }
        return "Erreur interne du serveur";
    }

    @Override
    protected String detail(Throwable ex) {
        if (ex instanceof WebApplicationException we) {
            return we.getMessage();
        }
        return "Une erreur inattendue est survenue lors du traitement de votre requête.";
    }
}
