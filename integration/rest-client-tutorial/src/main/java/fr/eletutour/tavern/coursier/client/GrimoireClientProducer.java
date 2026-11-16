package fr.eletutour.tavern.coursier.client;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Construit le client du Grimoire à l'exécution.
 *
 * <p>C'est l'alternative au client déclaratif : utile quand l'adresse dépend
 * de l'environnement, d'un tenant ou d'une découverte de service.</p>
 */
@ApplicationScoped
public class GrimoireClientProducer {

    @ConfigProperty(name = "taverne.grimoire.url")
    URI baseUri;

    @Produces
    @Singleton
    GrimoireClient grimoireClient() {
        return QuarkusRestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build(GrimoireClient.class);
    }
}
