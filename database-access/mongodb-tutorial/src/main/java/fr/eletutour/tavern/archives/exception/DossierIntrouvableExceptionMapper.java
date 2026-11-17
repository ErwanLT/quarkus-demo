package fr.eletutour.tavern.archives.exception;

import fr.eletutour.tavern.archives.dto.Probleme;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DossierIntrouvableExceptionMapper implements ExceptionMapper<DossierIntrouvableException> {

    @Override
    public Response toResponse(DossierIntrouvableException exception) {
        return Probleme.reponse(404, "Dossier introuvable", exception.getMessage());
    }
}
