package fr.eletutour.tavern.barde.resource;

import fr.eletutour.tavern.barde.instrument.InstrumentInconnuException;
import fr.eletutour.tavern.barde.repertoire.BalladeIntrouvableException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.Map;

/**
 * Transforme les erreurs du barde en 404 lisibles, pour voir clairement
 * ce qui manque quand le binaire natif a « oublié » une classe ou un fichier.
 */
public class ParcheminsExceptionMapper {

    @ServerExceptionMapper
    public Response instrumentInconnu(InstrumentInconnuException e) {
        return introuvable(e.getMessage());
    }

    @ServerExceptionMapper
    public Response balladeIntrouvable(BalladeIntrouvableException e) {
        return introuvable(e.getMessage());
    }

    private static Response introuvable(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("erreur", message))
                .build();
    }
}
