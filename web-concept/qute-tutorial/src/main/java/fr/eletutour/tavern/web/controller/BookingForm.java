package fr.eletutour.tavern.web.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

public class BookingForm {
    @RestForm
    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom est trop long (100 caractères max).")
    public String adventurerName;

    @RestForm
    @NotBlank(message = "La date d'arrivée est obligatoire.")
    public String arrivalDate;

    @RestForm
    @Min(value = 1, message = "Le nombre de nuits doit être au moins 1.")
    public int nights = 1;

    @RestForm
    @NotBlank(message = "Le type de suite est obligatoire.")
    public String roomType;
}
