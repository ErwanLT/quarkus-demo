package fr.eletutour.tavern.raven;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie le sort d'un corbeau qui se pose au colombier, sans broker Kafka
 * ni Docker : le canal {@code raven-inbound} passe par le connecteur
 * in-memory (cf. {@code src/test/resources/application.properties}).
 * <p>
 * Le routage vers la DLQ est assure par le connecteur Kafka lui-meme a la
 * reception du {@code nack} : le test verifie donc ce qui releve de
 * l'application (retries puis rejet du message) et que la strategie
 * d'echec {@code dead-letter-queue} est bien configuree.
 */
@QuarkusTest
class RavenRetryDlqTest {

    /** 3 retries espaces de 1 s (jitter de 200 ms compris) : au moins 2,4 s. */
    private static final Duration DUREE_MINIMALE_DES_RETRIES = Duration.ofMillis(2400);

    @Inject
    @Connector(InMemoryConnector.CONNECTOR)
    InMemoryConnector connector;

    private InMemorySource<Message<RavenMessage>> colombier;

    @BeforeEach
    void setUp() {
        colombier = connector.source("raven-inbound");
    }

    @Test
    @DisplayName("Un corbeau porteur d'un message sain est accueilli et acquitte")
    void should_ack_valid_raven() throws Exception {
        // Given
        CompletableFuture<Void> ack = new CompletableFuture<>();
        CompletableFuture<Throwable> nack = new CompletableFuture<>();
        RavenMessage message = RavenMessage.from("Tavernier", "La biere est arrivee");

        // When
        colombier.send(corbeau(message, ack, nack));

        // Then
        ack.get(5, TimeUnit.SECONDS);
        assertFalse(nack.isDone());
    }

    @Test
    @DisplayName("Un corbeau porteur d'une erreur est retente puis rejete vers la DLQ")
    void should_retry_then_nack_failing_raven() throws Exception {
        // Given
        CompletableFuture<Void> ack = new CompletableFuture<>();
        CompletableFuture<Throwable> nack = new CompletableFuture<>();
        RavenMessage message = RavenMessage.from("Tavernier", "Ce message contient une erreur fatale");
        long debut = System.nanoTime();

        // When
        colombier.send(corbeau(message, ack, nack));

        // Then
        Throwable cause = nack.get(15, TimeUnit.SECONDS);
        Duration duree = Duration.ofNanos(System.nanoTime() - debut);

        assertInstanceOf(RuntimeException.class, cause);
        assertTrue(cause.getMessage().contains(message.content()));
        assertFalse(ack.isDone());
        assertTrue(duree.compareTo(DUREE_MINIMALE_DES_RETRIES) >= 0,
                "Le message aurait du etre retente avant d'etre rejete (duree : " + duree.toMillis() + " ms)");
    }

    @Test
    @DisplayName("La strategie d'echec du colombier envoie les corbeaux rejetes en DLQ")
    void should_configure_dead_letter_queue() {
        var config = ConfigProvider.getConfig();

        assertEquals("dead-letter-queue",
                config.getValue("mp.messaging.incoming.raven-inbound.failure-strategy", String.class));
        assertEquals("raven-roost-dlq",
                config.getValue("mp.messaging.incoming.raven-inbound.dead-letter-queue.topic", String.class));
    }

    private static Message<RavenMessage> corbeau(RavenMessage message,
                                                 CompletableFuture<Void> ack,
                                                 CompletableFuture<Throwable> nack) {
        return Message.of(message,
                () -> {
                    ack.complete(null);
                    return CompletableFuture.completedFuture(null);
                },
                cause -> {
                    nack.complete(cause);
                    return CompletableFuture.completedFuture(null);
                });
    }
}
