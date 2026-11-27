package fr.eletutour.fitnesse.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Le registre des réservations, gardé en mémoire : il est remis à zéro avant
 * chaque page FitNesse (voir la page SetUp de la suite).
 */
@ApplicationScoped
public class Registre {

    private final Map<Integer, Reservation> reservations = new ConcurrentHashMap<>();
    private final AtomicInteger prochainNumero = new AtomicInteger(1);

    @Inject
    Tarif tarif;

    public synchronized Reservation reserver(String nom, String codeChambre, int nuits, boolean membre) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        Chambre chambre = Chambre.depuis(codeChambre);
        if (disponibles(chambre) == 0) {
            throw new ChambreComplete(chambre);
        }
        Devis devis = tarif.devis(chambre, nuits, membre);
        Reservation reservation = new Reservation(
                prochainNumero.getAndIncrement(), nom.trim(), chambre, nuits, membre, devis.total());
        reservations.put(reservation.numero(), reservation);
        return reservation;
    }

    public Optional<Reservation> trouver(int numero) {
        return Optional.ofNullable(reservations.get(numero));
    }

    public int disponibles(Chambre chambre) {
        long occupees = reservations.values().stream().filter(r -> r.chambre() == chambre).count();
        return chambre.capacite() - (int) occupees;
    }

    /** Le tavernier efface l'ardoise : utilisé par les tests d'acceptation. */
    public synchronized void effacer() {
        reservations.clear();
        prochainNumero.set(1);
    }
}
