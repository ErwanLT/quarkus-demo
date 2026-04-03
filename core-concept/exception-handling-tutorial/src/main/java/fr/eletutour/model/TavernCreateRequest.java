package fr.eletutour.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TavernCreateRequest(
    @NotBlank(message = "Le nom de la taverne est requis")
    String name,
    
    @NotBlank(message = "La ville est requise")
    String city,
    
    @Min(value = 1, message = "La capacité doit être d'au moins 1 personne")
    @Max(value = 500, message = "La capacité ne peut excéder 500 personnes")
    int maxCapacity
) {}
