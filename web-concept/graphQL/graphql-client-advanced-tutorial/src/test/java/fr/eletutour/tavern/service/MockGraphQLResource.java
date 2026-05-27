package fr.eletutour.tavern.service;

import io.quarkus.test.junit.QuarkusTestProfile;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/mock-graphql")
public class MockGraphQLResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> handle(Map<String, Object> request) {
        return Map.of("data", Map.of(
                "aventuriers", java.util.List.of(
                        Map.of("nom", "Baldric", "niveau", 7),
                        Map.of("nom", "Elara", "niveau", 5)
                )
        ));
    }

    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.smallrye-graphql-client.tavern-dynamic.url",
                    "http://localhost:${quarkus.http.test-port:8081}/mock-graphql");
        }
    }
}
