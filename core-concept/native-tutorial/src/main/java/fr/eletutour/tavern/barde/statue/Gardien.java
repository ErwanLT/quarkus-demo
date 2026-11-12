package fr.eletutour.tavern.barde.statue;

import io.quarkus.runtime.ImageMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.time.Instant;

/**
 * Le gardien note l'heure à laquelle la statue se réveille et sait dire
 * si elle est de pierre (binaire natif) ou de chair (JVM).
 */
@ApplicationScoped
public class Gardien {

    private Instant reveil;

    void auReveil(@Observes StartupEvent event) {
        reveil = Instant.now();
    }

    public Statue observer() {
        String mode = ImageMode.current().isNativeImage() ? "natif" : "jvm";
        return new Statue(mode, Sculpteur.DATE_GRAVEE, reveil);
    }
}
