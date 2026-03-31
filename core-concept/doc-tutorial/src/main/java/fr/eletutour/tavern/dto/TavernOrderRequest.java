package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Commande a la taverne")
public record TavernOrderRequest(
        @Schema(description = "Objet commande", example = "Healing Potion")
        String item,
        @Schema(description = "Quantite", example = "2")
        int quantity
) {
}
