package fr.eletutour.tavern.sse.controller;

import fr.eletutour.tavern.sse.model.Annonce;
import fr.eletutour.tavern.sse.model.CriDuBarman;
import fr.eletutour.tavern.sse.model.PanneauMagique;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/taverne")
public class SalleCommuneResource {

    @Inject
    PanneauMagique panneau;

    // Le panneau lui-même : les aventuriers s'y abonnent
    // et reçoivent les annonces au fil de l'eau, sans jamais
    // relancer la conversation avec le barman.
    @GET
    @Path("/panneau")
    @Produces(MediaType.SERVER_SENT_EVENTS + "; charset=UTF-8")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Annonce> suivreLePanneau() {
        return panneau.observer();
    }

    // Le barman crie une annonce depuis le comptoir :
    // taverne d'à côté, prime sur un dragon, tournée gratuite...
    @POST
    @Path("/crier")
    @Consumes(MediaType.APPLICATION_JSON)
    public void crierAnnonce(CriDuBarman cri) {
        panneau.afficher(Annonce.duBarman(cri.message()));
    }
}