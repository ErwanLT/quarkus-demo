package fr.eletutour.tavern.coursier.exception.business;

import fr.eletutour.tavern.coursier.model.Difficulte;

import java.util.Arrays;
import java.util.Map;

/**
 * Erreur métier du coursier. Les fabriques nomment chaque situation ; le
 * reste du code n'a jamais à manipuler un code HTTP.
 */
public class CoursierException extends BusinessException {

    private final CoursierError error;
    private final Map<String, Object> details;

    private CoursierException(CoursierError error, Map<String, Object> details, Object... detailArgs) {
        super(error.getStatus(), error.getTitle(), error.formatDetail(detailArgs));
        this.error = error;
        this.details = details;
    }

    public static CoursierException contratIntrouvable() {
        return new CoursierException(CoursierError.CONTRAT_INTROUVABLE, Map.of());
    }

    public static CoursierException recetteIntrouvable() {
        return new CoursierException(CoursierError.RECETTE_INTROUVABLE, Map.of());
    }

    public static CoursierException difficulteInconnue(String valeur) {
        return new CoursierException(CoursierError.DIFFICULTE_INCONNUE, Map.of(),
                valeur, Arrays.toString(Difficulte.values()));
    }

    /**
     * Le service distant a répondu par une erreur que le client ne sait pas
     * traduire : ce n'est pas la faute de l'appelant, d'où le 502.
     */
    public static CoursierException reponseInattendue(String service, int statutDistant) {
        return new CoursierException(CoursierError.REPONSE_INATTENDUE,
                Map.of("service", service, "statutDistant", statutDistant),
                service, statutDistant);
    }

    public CoursierError getError() {
        return error;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
