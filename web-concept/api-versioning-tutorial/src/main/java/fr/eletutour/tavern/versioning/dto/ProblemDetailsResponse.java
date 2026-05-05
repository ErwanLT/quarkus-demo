package fr.eletutour.tavern.versioning.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Erreur standardisee au format Problem Details avec une extension metier code.")
public record ProblemDetailsResponse(
        @Schema(examples = "https://eletutour.fr/problems/api-version-unknown")
        String type,

        @Schema(examples = "Version d'API inconnue")
        String title,

        @Schema(examples = "400")
        int status,

        @Schema(examples = "Le grimoire d'API ne connait que les versions 1 et 2.")
        String detail,

        @Schema(examples = "VERSION_INCONNUE")
        String code
) {
}
