package fr.eletutour.validation.model;

import java.time.LocalDate;

public record Reservation(
        String reference,
        String aventurier,
        TypeChambre chambre,
        LocalDate arrivee,
        LocalDate depart,
        int aventuriers
) {}
