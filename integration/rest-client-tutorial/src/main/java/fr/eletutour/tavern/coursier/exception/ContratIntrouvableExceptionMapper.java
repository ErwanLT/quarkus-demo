package fr.eletutour.tavern.coursier.exception;

import fr.eletutour.tavern.coursier.model.Probleme;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ContratIntrouvableExceptionMapper implements ExceptionMapper<ContratIntrouvableException> {

    @Override
    public Response toResponse(ContratIntrouvableException exception) {
        return Probleme.reponse(404, "Contrat introuvable", exception.getMessage());
    }
}
