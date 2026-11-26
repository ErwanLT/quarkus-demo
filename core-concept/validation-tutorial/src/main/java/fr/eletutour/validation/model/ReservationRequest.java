package fr.eletutour.validation.model;

import fr.eletutour.validation.constraint.SejourCoherent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@SejourCoherent
public record ReservationRequest(

        @NotBlank(message = "Le nom de l'aventurier est requis")
        @Size(min = 2, max = 40, message = "Le nom doit comporter entre 2 et 40 caractères")
        String aventurier,

        @NotBlank(message = "Le courriel est requis")
        @Email(message = "Le courriel doit être une adresse valide")
        String courriel,

        @NotNull(message = "Le type de chambre est requis")
        TypeChambre chambre,

        @NotNull(message = "La date d'arrivée est requise")
        @FutureOrPresent(message = "L'arrivée doit être aujourd'hui ou plus tard")
        LocalDate arrivee,

        @NotNull(message = "La date de départ est requise")
        @Future(message = "Le départ doit être dans le futur")
        LocalDate depart,

        @Min(value = 1, message = "Au moins un aventurier est nécessaire")
        @Max(value = 4, message = "Une chambre accueille au plus 4 aventuriers")
        int aventuriers

) {}
