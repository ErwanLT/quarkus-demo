package fr.eletutour.tavern.batch.resource.error;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Problem", description = "Representation d'erreur standard RFC 7807")
public record ApiProblem(
    @Schema(example = "https://tavern.eletutour.fr/problems/invalid-request") String type,
    @Schema(example = "Requete invalide") String title,
    @Schema(example = "400") int status,
    @Schema(example = "Le champ orders est obligatoire") String detail,
    @Schema(example = "/tavern/brewery/day-consumptions") String instance
) {
}
