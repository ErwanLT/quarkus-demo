package fr.eletutour.tavern.service;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.smallrye.graphql.client.core.Document.document;
import static io.smallrye.graphql.client.core.Field.field;
import static io.smallrye.graphql.client.core.Operation.operation;

@Path("/tavern")
public class TavernService {

    @Inject
    @GraphQLClient("tavern-dynamic")
    DynamicGraphQLClient dynamicClient;


    @GET
    @Path("/dynamic")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public List<Map> getAllAventurier() throws ExecutionException, InterruptedException {
        Document query = document(
                operation(
                        field("aventuriers",
                                field("nom"),
                                field("niveau"),
                                field("quetes",
                                        field("titre"),
                                        field("recompenseOr")
                                )
                        )
                )
        );
        Response response = dynamicClient.executeSync(query);
        return response.getList(Map.class, "aventuriers");
    }

}
