package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Recu de commande")
public record TavernOrderResponse(
        @Schema(description = "Objet commande")
        String item,
        @Schema(description = "Quantite")
        int quantity,
        @Schema(description = "Message de confirmation")
        String note
) {
}
