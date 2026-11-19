package fr.eletutour.tavern.archives.exception.mapper;

import com.mongodb.MongoWriteException;
import fr.eletutour.tavern.archives.exception.business.ArchivesException;
import fr.eletutour.tavern.archives.exception.model.Problem;
import jakarta.ws.rs.ext.Provider;

/**
 * Filet de sécurité de l'index unique : deux requêtes peuvent passer la
 * vérification applicative en même temps, MongoDB refuse alors la seconde
 * écriture ({@code code 11000}). L'erreur est traduite en {@code 409} avec le
 * même format {@code Problem} que les erreurs métier.
 */
@Provider
public class MongoWriteExceptionMapper extends AbstractExceptionMapper<MongoWriteException> {

    private static final int DUPLICATE_KEY = 11000;

    @Override
    protected int status(MongoWriteException exception) {
        return erreurMetier(exception).getStatus();
    }

    @Override
    protected String title(MongoWriteException exception) {
        return erreurMetier(exception).getTitle();
    }

    @Override
    protected String type(MongoWriteException exception) {
        return BusinessExceptionMapper.typeUrn(erreurMetier(exception));
    }

    @Override
    protected String detail(MongoWriteException exception) {
        return erreurMetier(exception).getMessage();
    }

    @Override
    protected void enrich(Problem problem, MongoWriteException exception) {
        BusinessExceptionMapper.enrichir(problem, erreurMetier(exception));
    }

    private ArchivesException erreurMetier(MongoWriteException exception) {
        if (exception.getError() != null && exception.getError().getCode() == DUPLICATE_KEY) {
            return ArchivesException.nomDejaPris();
        }
        return ArchivesException.ecritureImpossible(
                exception.getError() == null ? -1 : exception.getError().getCode());
    }
}
