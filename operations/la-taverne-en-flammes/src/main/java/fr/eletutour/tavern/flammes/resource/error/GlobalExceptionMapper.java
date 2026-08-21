package fr.eletutour.tavern.flammes.resource.error;

import jakarta.transaction.RollbackException;
import jakarta.transaction.TransactionalException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Traduit les echecs de l'incendie en reponses {@code application/problem+json}.
 *
 * <p>Les scenarios de rollback sont des echecs attendus : il faut qu'ils remontent proprement
 * au client au lieu de laisser fuir une stacktrace.</p>
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);
    private static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(RuntimeException exception) {
        // Narayana annule la transaction depuis son thread de surveillance : l'echec remonte
        // enveloppe, il faut donc parcourir la chaine de causes pour le reconnaitre.
        if (estAnnulationDeTransaction(exception)) {
            LOG.warnv("Transaction annulee par le gestionnaire JTA : {0}", exception.getMessage());
            return versProblem(
                Response.Status.SERVICE_UNAVAILABLE,
                "https://tavern.eletutour.fr/problems/transaction-rollback",
                "Transaction annulee",
                "La transaction a depasse son timeout, aucune ecriture n'a ete conservee"
            );
        }

        return switch (exception) {
            case IllegalArgumentException illegalArgumentException -> {
                LOG.warnv("Requete invalide : {0}", illegalArgumentException.getMessage());
                yield versProblem(
                    Response.Status.BAD_REQUEST,
                    "https://tavern.eletutour.fr/problems/invalid-request",
                    "Requete invalide",
                    illegalArgumentException.getMessage()
                );
            }
            case IllegalStateException illegalStateException -> {
                LOG.error("Echec interne pendant l'incendie", illegalStateException);
                yield versProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "https://tavern.eletutour.fr/problems/internal-error",
                    "Erreur interne",
                    illegalStateException.getMessage()
                );
            }
            default -> {
                LOG.error("Exception inattendue pendant l'incendie", exception);
                yield versProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "https://tavern.eletutour.fr/problems/unexpected-error",
                    "Erreur inattendue",
                    "Une erreur inattendue est survenue"
                );
            }
        };
    }

    private boolean estAnnulationDeTransaction(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof RollbackException || cause instanceof TransactionalException) {
                return true;
            }
            if (cause.getCause() == cause) {
                break;
            }
        }
        return false;
    }

    private Response versProblem(Response.Status status, String type, String title, String detail) {
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
