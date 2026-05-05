package fr.eletutour.tavern.versioning.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Contrat V1 historique du menu, conserve pour les anciens clients.")
public record MenuV1Response(
        @Schema(examples = "Ragout de sanglier")
        String plat,

        @Schema(examples = "12")
        int prixPiecesCuivre
) implements MenuResponse {
}
