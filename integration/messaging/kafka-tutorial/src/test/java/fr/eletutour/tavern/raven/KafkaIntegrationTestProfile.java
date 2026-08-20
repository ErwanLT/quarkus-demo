package fr.eletutour.tavern.raven;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

public class KafkaIntegrationTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            // Use real Kafka connectors for testing retry and DLQ
            "mp.messaging.outgoing.raven-outbound.connector", "smallrye-kafka",
            "mp.messaging.incoming.raven-inbound.connector", "smallrye-kafka",
            
            // Configure DLQ consumer channel in test
            "mp.messaging.incoming.raven-dlq-in.connector", "smallrye-kafka",
            "mp.messaging.incoming.raven-dlq-in.topic", "raven-roost-dlq",
            "mp.messaging.incoming.raven-dlq-in.value.deserializer", "fr.eletutour.tavern.raven.RavenMessageDeserializer",
            "mp.messaging.incoming.raven-dlq-in.auto.offset.reset", "earliest"
        );
    }
}
