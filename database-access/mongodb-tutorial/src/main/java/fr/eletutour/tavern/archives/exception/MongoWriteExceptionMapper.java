package fr.eletutour.tavern.archives.exception;

import com.mongodb.MongoWriteException;
import fr.eletutour.tavern.archives.dto.Probleme;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Filet de sécurité : l'index unique sur {@code nom} peut refuser une écriture
 * même si la vérification applicative est passée (course entre deux requêtes).
 */
@Provider
public class MongoWriteExceptionMapper implements ExceptionMapper<MongoWriteException> {

    private static final int DUPLICATE_KEY = 11000;

    @Override
    public Response toResponse(MongoWriteException exception) {
        if (exception.getError() != null && exception.getError().getCode() == DUPLICATE_KEY) {
            return Probleme.reponse(409, "Nom déjà pris",
                    "Un dossier porte déjà ce nom dans les archives.");
        }
        return Probleme.reponse(500, "Écriture impossible",
                "MongoDB a refusé l'écriture du dossier.");
    }
}
