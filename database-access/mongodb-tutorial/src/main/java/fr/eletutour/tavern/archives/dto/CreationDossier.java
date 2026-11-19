package fr.eletutour.tavern.archives.dto;

import fr.eletutour.tavern.archives.model.ClasseAventurier;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreationDossier(
        @NotBlank @Size(max = 100) String nom,
        @NotNull
        ClasseAventurier classe,
        @Min(1) int niveau,
        @Min(0) int bourse) {
}
