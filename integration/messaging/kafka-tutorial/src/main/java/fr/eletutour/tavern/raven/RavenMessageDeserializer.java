package fr.eletutour.tavern.raven;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

/**
 * Deserializer du colombier : transforme les octets recus sur le topic Kafka
 * en {@link RavenMessage} exploitable.
 */
public class RavenMessageDeserializer extends ObjectMapperDeserializer<RavenMessage> {

    public RavenMessageDeserializer() {
        super(RavenMessage.class);
    }
}
