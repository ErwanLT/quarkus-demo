package fr.eletutour.tavern.scheduler.resource;

import fr.eletutour.tavern.scheduler.model.EtatCorvee;
import fr.eletutour.tavern.scheduler.model.TacheCorvee;
import fr.eletutour.tavern.scheduler.resource.dto.JobPlanifie;
import fr.eletutour.tavern.scheduler.service.RegistreDesCorvees;
import fr.eletutour.tavern.scheduler.task.CorveesProgrammees;
import io.quarkus.scheduler.Scheduler;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/corvees")
@Produces(MediaType.APPLICATION_JSON)
public class CorveesResource {

    @Inject
    RegistreDesCorvees registre;

    @Inject
    CorveesProgrammees corvees;

    @Inject
    Scheduler scheduler;

    /** État des corvées déjà passées. */
    @GET
    public List<EtatCorvee> etats() {
        return registre.etats();
    }

    /**
     * Déclenchements enregistrés par Quarkus. Quand le scheduler est désactivé,
     * {@link Scheduler#getScheduledJobs()} lève une exception : on renvoie une
     * liste vide plutôt qu'une erreur 500.
     */
    @GET
    @Path("/taches")
    public List<JobPlanifie> taches() {
        if (!scheduler.isStarted()) {
            return List.of();
        }
        return scheduler.getScheduledJobs().stream()
                .map(JobPlanifie::depuis)
                .toList();
    }

    /** Force une corvée sans attendre son échéance : pratique en dev et en démo. */
    @POST
    @Path("/{tache}")
    public EtatCorvee executer(@PathParam("tache") TacheCorvee tache) {
        corvees.executer(tache);
        return registre.etats().stream()
                .filter(etat -> etat.tache().equals(tache.identifiant()))
                .findFirst()
                .orElseThrow();
    }
}
