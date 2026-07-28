package fr.eletutour.tavern.reactive.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Chope servie par le tavernier reactif.")
public record BeerResponse(
        @Schema(description = "Aventurier servi.", example = "Gimli")
        String adventurer,
        @Schema(description = "Boisson servie.", example = "Biere ambrée du Dragon Dormant")
        String drink,
        @Schema(description = "Temps necessaire pour remplir la chope.", example = "300")
        long durationMs,
        @Schema(description = "Message narratif de la taverne.")
        String message
) {
}
