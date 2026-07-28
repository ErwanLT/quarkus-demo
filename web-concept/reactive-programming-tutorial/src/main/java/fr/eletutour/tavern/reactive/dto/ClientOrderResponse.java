package fr.eletutour.tavern.reactive.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Commande emise par un client dans le flux reactif.")
public record ClientOrderResponse(
        @Schema(description = "Position du client dans le flux.", example = "1")
        long sequence,
        @Schema(description = "Nom du client.", example = "Client 1")
        String client,
        @Schema(description = "Commande demandee.", example = "Hydromel")
        String order,
        @Schema(description = "Message narratif de la taverne.")
        String message
) {
}
