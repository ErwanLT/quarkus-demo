package fr.eletutour.tavern.coursier.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.Response;

import java.util.Map;

/**
 * Erreur au format RFC 9457 ({@code application/problem+json}).
 *
 * <p>Le coursier la produit via ses {@code ExceptionMapper} ; la Guilde et le
 * Grimoire simulés l'utilisent aussi pour répondre comme de vrais services.</p>
 *
 * @param type       URN qui identifie la famille d'erreur
 * @param title      résumé lisible, stable pour un même type
 * @param status     code HTTP
 * @param detail     explication propre à cette occurrence
 * @param instance   URI de la requête fautive
 * @param timestamp  instant de l'erreur, en UTC
 * @param additional informations complémentaires (code métier, statut distant…)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Probleme(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String timestamp,
        Map<String, Object> additional) {

    public static final String MEDIA_TYPE = "application/problem+json";

    /**
     * Raccourci pour les services simulés : un problème minimal, déjà emballé
     * dans une réponse HTTP.
     */
    public static Response reponse(int status, String title, String detail) {
        return new Probleme("about:blank", title, status, detail, null, null, null).enReponse();
    }

    public Response enReponse() {
        return Response.status(status)
                .type(MEDIA_TYPE)
                .entity(this)
                .build();
    }
}
