package fr.eletutour.tavern.coursier.exception.mapper;

import fr.eletutour.tavern.coursier.exception.business.CoursierError;
import io.quarkus.logging.Log;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

/**
 * Le REST Client lève une {@link ProcessingException} quand il n'obtient
 * aucune réponse exploitable : connexion refusée, délai dépassé, corps
 * illisible. Le coursier a joué le rôle de passerelle et le service distant
 * a failli : 502.
 *
 * <p>La cause technique (hôte, port, pile) part dans les logs, jamais dans la
 * réponse.</p>
 */
@Provider
public class ServiceInjoignableExceptionMapper extends AbstractExceptionMapper<ProcessingException> {

    private static final CoursierError ERREUR = CoursierError.SERVICE_INJOIGNABLE;

    @Override
    protected int status(ProcessingException ex) {
        return ERREUR.getStatus();
    }

    @Override
    protected String title(ProcessingException ex) {
        return ERREUR.getTitle();
    }

    @Override
    protected String type(ProcessingException ex) {
        return "urn:problem:coursier:" + ERREUR.getCode();
    }

    @Override
    protected String detail(ProcessingException ex) {
        Log.warnf("Service distant injoignable : %s", ex.getMessage());
        return ERREUR.formatDetail();
    }

    @Override
    protected Map<String, Object> additional(ProcessingException ex) {
        return Map.of("code", ERREUR.getCode());
    }
}
