package fr.eletutour.tavern.batch.resource.dto;

import fr.eletutour.tavern.batch.model.BeerStyle;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "DayConsumptionRequest", description = "Demandes de bieres de la journee, par type de biere.")
public record DayConsumptionRequestDto(
    @NotNull(message = "Le champ orders est obligatoire")
    @NotEmpty(message = "Le champ orders ne peut pas etre vide")
    @Schema(
        description = "Quantite demandee par biere. La quantite doit etre superieure ou egale a 0.",
        example = "{\"DRAGON_STOUT\":30,\"ELVEN_IPA\":20}"
    )
    Map<@NotNull(message = "Chaque commande doit cibler un type de biere valide") BeerStyle,
        @NotNull(message = "Chaque quantite de biere est obligatoire")
        @PositiveOrZero(message = "Les quantites de biere doivent etre positives ou nulles") Integer> orders
) {
}
