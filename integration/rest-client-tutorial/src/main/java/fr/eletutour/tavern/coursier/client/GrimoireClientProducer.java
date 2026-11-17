package fr.eletutour.tavern.coursier.client;

import fr.eletutour.tavern.coursier.config.GrimoireConfig;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Construit le client du Grimoire à l'exécution.
 *
 * <p>C'est l'alternative au client déclaratif : utile quand l'adresse dépend
 * de l'environnement, d'un tenant ou d'une découverte de service.</p>
 */
@ApplicationScoped
public class GrimoireClientProducer {

    @Inject
    GrimoireConfig config;

    @Produces
    @Singleton
    GrimoireClient grimoireClient() {
        return QuarkusRestClientBuilder.newBuilder()
                .baseUri(config.url())
                .connectTimeout(config.connectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(config.readTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .build(GrimoireClient.class);
    }

    /**
     * Ce que l'on construit soi-même, on le ferme soi-même : le pool de
     * connexions du client est libéré à l'arrêt de l'application.
     */
    void fermer(@Disposes GrimoireClient client) throws IOException {
        client.close();
    }
}
