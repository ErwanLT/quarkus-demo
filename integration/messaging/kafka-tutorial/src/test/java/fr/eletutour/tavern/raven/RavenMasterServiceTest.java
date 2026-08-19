package fr.eletutour.tavern.raven;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifie que le maitre corbeau publie correctement un message sur le canal
 * sortant, sans dependre d'un vrai broker Kafka (connecteur in-memory).
 * <p>
 * Given : un message a transmettre par un expediteur donne
 * When : le maitre corbeau depeche le corbeau
 * Then : le message est retrouve, intact, sur le canal de sortie simule
 */
@QuarkusTest
class RavenMasterServiceTest {

    @Inject
    RavenMasterService ravenMasterService;

    @Inject
    @Connector(InMemoryConnector.CONNECTOR)
    InMemoryConnector connector;

    private InMemorySink<RavenMessage> ravenRoost;

    @BeforeEach
    void setUp() {
        ravenRoost = connector.sink("raven-outbound");
    }

    @Test
    void should_dispatch_raven_with_sender_and_content() {
        // Given
        String sender = "Tavernier";
        String content = "La biere est arrivee";

        // When
        RavenMessage dispatched = ravenMasterService.dispatch(sender, content);

        // Then
        assertNotNull(dispatched.sentAt());
        assertEquals(1, ravenRoost.received().size());

        RavenMessage published = ravenRoost.received().get(0).getPayload();
        assertEquals(sender, published.sender());
        assertEquals(content, published.content());
    }
}