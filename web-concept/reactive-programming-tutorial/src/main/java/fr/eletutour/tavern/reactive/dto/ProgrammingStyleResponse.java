package fr.eletutour.tavern.reactive.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Comparaison entre un raisonnement imperatif et un raisonnement reactif.")
public record ProgrammingStyleResponse(
        @Schema(description = "Vision imperative bloquante.")
        String imperative,
        @Schema(description = "Vision reactive non bloquante.")
        String reactive,
        @Schema(description = "Role de Uni dans la metaphore.")
        String uni,
        @Schema(description = "Role de Multi dans la metaphore.")
        String multi
) {
}
