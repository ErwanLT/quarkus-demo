package fr.eletutour.tavern.coursier.exception;

import fr.eletutour.tavern.coursier.model.Probleme;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RecetteIntrouvableExceptionMapper implements ExceptionMapper<RecetteIntrouvableException> {

    @Override
    public Response toResponse(RecetteIntrouvableException exception) {
        return Probleme.reponse(404, "Recette introuvable", exception.getMessage());
    }
}
