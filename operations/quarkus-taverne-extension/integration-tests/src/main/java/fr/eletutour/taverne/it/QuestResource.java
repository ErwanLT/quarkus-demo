package fr.eletutour.taverne.it;

import fr.eletutour.taverne.runtime.Taverne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Un service quelconque du royaume. Il ne connaît rien de la forge, rien du
 * BuildStep, rien du module deployment : il pose juste {@code @Taverne} sur
 * sa méthode, comme n'importe quel aventurier utiliserait une relique sans
 * savoir comment elle a été forgée.
 */
@Path("/quete")
@ApplicationScoped
public class QuestResource {

    @GET
    @Taverne(quete = "Explorer le donjon de la Taverne")
    public String explorer() {
        return "Le héros trouve un coffre. Il contient... un README.";
    }
}
