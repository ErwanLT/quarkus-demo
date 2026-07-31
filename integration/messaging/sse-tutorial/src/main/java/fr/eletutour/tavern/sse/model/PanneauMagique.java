package fr.eletutour.tavern.sse.model;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanneauMagique {

    // Le processor est le coeur du panneau : il retient
    // les abonnés (les aventuriers attablés) et leur
    // pousse chaque annonce dès qu'elle arrive.
    private final BroadcastProcessor<Annonce> flux = BroadcastProcessor.create();

    public void afficher(Annonce annonce) {
        flux.onNext(annonce);
    }

    public Multi<Annonce> observer() {
        return flux;
    }
}