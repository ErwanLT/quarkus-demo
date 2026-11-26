package fr.eletutour.validation.service;

import fr.eletutour.validation.business.TavernException;
import fr.eletutour.validation.model.Reservation;
import fr.eletutour.validation.model.ReservationRequest;
import fr.eletutour.validation.repository.ReservationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class ReservationService {

    @Inject
    ReservationRepository repository;

    private final AtomicInteger sequence = new AtomicInteger();

    public List<Reservation> lister() {
        return repository.findAll();
    }

    /**
     * Le contrôle et l'enregistrement sont dans la même méthode synchronisée :
     * deux requêtes concurrentes ne peuvent pas toutes deux passer le test de
     * chevauchement. En cluster, cette garantie locale ne suffit pas : il faut
     * une contrainte en base ou un verrou distribué.
     */
    public synchronized Reservation reserver(ReservationRequest request) {
        // Règle métier : elle s'exécute après la validation, et produit une
        // erreur 409 problem+json via le BusinessExceptionMapper.
        if (repository.chevauche(request.chambre(), request.arrivee(), request.depart())) {
            throw TavernException.chambreDejaReservee(request.chambre(), request.arrivee());
        }

        Reservation reservation = new Reservation(
                "RES-" + String.format("%04d", sequence.incrementAndGet()),
                request.aventurier(),
                request.chambre(),
                request.arrivee(),
                request.depart(),
                request.aventuriers());
        return repository.save(reservation);
    }
}
