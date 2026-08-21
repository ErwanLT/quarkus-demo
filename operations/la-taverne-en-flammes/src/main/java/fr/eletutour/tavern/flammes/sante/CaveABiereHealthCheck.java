package fr.eletutour.tavern.flammes.sante;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Readiness : peut-on encore entrer commander une chope ?
 *
 * <p>Si la cuisine brule, l'acces a la reserve de biere est coupe : la taverne tient encore
 * debout mais elle n'est plus en etat de servir. L'orchestrateur doit la sortir du routage.</p>
 */
@Readiness
@ApplicationScoped
public class CaveABiereHealthCheck implements HealthCheck {

    @Inject
    IncendieService incendieService;

    @Override
    public HealthCheckResponse call() {
        EtatTaverne etat = incendieService.etat();
        boolean accesCaveOk = !etat.cuisineEnFeu();

        return HealthCheckResponse.named("Acces a la reserve de biere")
            .status(accesCaveOk)
            .withData("temperature_cave", accesCaveOk ? "12 C" : "68 C")
            .withData("escalier_praticable", accesCaveOk)
            .withData("origine_incendie", etat.origineIncendie())
            .build();
    }
}
