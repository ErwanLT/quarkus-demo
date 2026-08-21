package fr.eletutour.tavern.flammes.sante;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Liveness : la taverne tient-elle encore debout ?
 *
 * <p>Une charpente rompue n'est pas rattrapable a chaud : l'orchestrateur doit detruire
 * l'instance et en demarrer une neuve.</p>
 */
@Liveness
@ApplicationScoped
public class CharpenteHealthCheck implements HealthCheck {

    @Inject
    IncendieService incendieService;

    @Override
    public HealthCheckResponse call() {
        EtatTaverne etat = incendieService.etat();

        return HealthCheckResponse.named("Charpente de la taverne")
            .status(etat.vivante())
            .withData("poutres_maitresses", etat.vivante() ? "intactes" : "rompues")
            .build();
    }
}
