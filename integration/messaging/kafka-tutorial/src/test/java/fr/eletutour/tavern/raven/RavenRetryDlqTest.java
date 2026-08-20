package fr.eletutour.tavern.raven;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(KafkaIntegrationTestProfile.class)
class RavenRetryDlqTest {

    @Inject
    RavenMasterService ravenMasterService;

    @Inject
    DlqConsumer dlqConsumer;

    @Test
    void testRetryAndDlq() throws InterruptedException {
        // Dispatch a message containing "erreur" to trigger failure and DLQ routing
        String sender = "Tavernier";
        String content = "Ce message contient une erreur fatale";
        
        dlqConsumer.clear();
        ravenMasterService.dispatch(sender, content);

        // Wait until the message is received by the DLQ consumer
        await(() -> dlqConsumer.getMessages().size() == 1);

        List<RavenMessage> dlqMessages = dlqConsumer.getMessages();
        assertEquals(1, dlqMessages.size());
        
        RavenMessage dlqMessage = dlqMessages.get(0);
        assertEquals(sender, dlqMessage.sender());
        assertEquals(content, dlqMessage.content());
    }

    private void await(Supplier<Boolean> condition) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (!condition.get()) {
            if (System.currentTimeMillis() - start > 15000) {
                throw new AssertionError("Condition not met within 15 seconds");
            }
            Thread.sleep(200);
        }
    }

    @ApplicationScoped
    public static class DlqConsumer {
        private final List<RavenMessage> messages = new CopyOnWriteArrayList<>();

        @Incoming("raven-dlq-in")
        public void consume(RavenMessage msg) {
            messages.add(msg);
        }

        public List<RavenMessage> getMessages() {
            return messages;
        }

        public void clear() {
            messages.clear();
        }
    }
}
