package fr.eletutour.tavern.flammes.resource.error;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Problem", description = "Representation d'erreur standard RFC 7807")
public record ApiProblem(
    @Schema(example = "https://tavern.eletutour.fr/problems/internal-error") String type,
    @Schema(example = "Erreur interne") String title,
    @Schema(example = "500") int status,
    @Schema(example = "Une poutre en flammes emporte le grand livre") String detail,
    @Schema(example = "/taverne/grand-livre/ecritures-interrompues") String instance
) {
}
