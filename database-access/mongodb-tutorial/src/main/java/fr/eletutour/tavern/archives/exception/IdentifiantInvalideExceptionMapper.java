package fr.eletutour.tavern.archives.exception;

import fr.eletutour.tavern.archives.dto.Probleme;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IdentifiantInvalideExceptionMapper implements ExceptionMapper<IdentifiantInvalideException> {

    @Override
    public Response toResponse(IdentifiantInvalideException exception) {
        return Probleme.reponse(400, "Identifiant invalide", exception.getMessage());
    }
}
