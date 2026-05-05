package fr.eletutour.tavern.versioning.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Prix structure de la V2, plus extensible que le champ V1 prixPiecesCuivre.")
public record PriceResponse(
        @Schema(examples = "12")
        int montant,

        @Schema(examples = "pieces-cuivre")
        String devise
) {
}
