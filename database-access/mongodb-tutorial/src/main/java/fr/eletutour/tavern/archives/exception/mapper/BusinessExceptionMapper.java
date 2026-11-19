package fr.eletutour.tavern.archives.exception.mapper;

import fr.eletutour.tavern.archives.exception.business.ArchivesException;
import fr.eletutour.tavern.archives.exception.business.BusinessException;
import fr.eletutour.tavern.archives.exception.model.Problem;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class BusinessExceptionMapper extends AbstractExceptionMapper<BusinessException> {

    @Override
    protected int status(BusinessException exception) {
        return exception.getStatus();
    }

    @Override
    protected String title(BusinessException exception) {
        return exception.getTitle();
    }

    @Override
    protected String type(BusinessException exception) {
        return typeUrn(exception);
    }

    @Override
    protected void enrich(Problem problem, BusinessException exception) {
        enrichir(problem, exception);
    }

    /** Partagé avec le mapper MongoDB pour garantir le même format de type. */
    static String typeUrn(BusinessException exception) {
        if (exception instanceof ArchivesException archives) {
            return "urn:problem:archives:" + archives.getError().getCode();
        }
        return "about:blank";
    }

    /** Partagé avec le mapper MongoDB pour garantir le même format d'enrichissement. */
    static void enrichir(Problem problem, BusinessException exception) {
        if (exception instanceof ArchivesException archives) {
            Map<String, Object> additional = new HashMap<>(archives.getContexte());
            additional.put("code", archives.getError().getCode());
            problem.additional(additional);
        }
    }
}
