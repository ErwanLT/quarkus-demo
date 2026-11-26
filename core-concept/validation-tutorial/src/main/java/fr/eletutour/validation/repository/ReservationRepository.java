package fr.eletutour.validation.repository;

import fr.eletutour.validation.model.Reservation;
import fr.eletutour.validation.model.TypeChambre;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ReservationRepository {

    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    /**
     * Deux séjours se chevauchent quand le début de l'un est avant la fin de
     * l'autre et inversement. Les intervalles sont semi-ouverts : un départ le
     * matin et une arrivée le même matin ne se chevauchent pas.
     */
    public boolean chevauche(TypeChambre chambre, LocalDate arrivee, LocalDate depart) {
        return reservations.values().stream().anyMatch(existante ->
                existante.chambre() == chambre
                        && arrivee.isBefore(existante.depart())
                        && existante.arrivee().isBefore(depart));
    }

    public Reservation save(Reservation reservation) {
        reservations.put(reservation.reference(), reservation);
        return reservation;
    }

    public List<Reservation> findAll() {
        return reservations.values().stream()
                .sorted(Comparator.comparing(Reservation::reference))
                .toList();
    }
}
