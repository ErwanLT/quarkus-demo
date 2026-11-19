package fr.eletutour.tavern.archives.dto;

import fr.eletutour.tavern.archives.model.Rang;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NouvelleProuesse(
        @NotBlank @Size(max = 150) String titre,
        @NotNull
        Rang rang,
        @Min(1) int annee,
        @Min(0) int recompense) {
}
