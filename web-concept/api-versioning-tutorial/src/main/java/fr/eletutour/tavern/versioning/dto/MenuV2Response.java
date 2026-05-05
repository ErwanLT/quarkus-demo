package fr.eletutour.tavern.versioning.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Contrat V2 enrichi du menu pour les clients de la guilde.")
public record MenuV2Response(
        @Schema(examples = "Ragout de sanglier aux herbes de druide")
        String plat,

        PriceResponse prix,

        @Schema(examples = "[\"sanglier\", \"orge\", \"carottes\", \"thym de druide\"]")
        List<String> ingredients,

        @Schema(examples = "aventuriers niveau 2 et plus")
        String disponiblePour
) implements MenuResponse {
}
