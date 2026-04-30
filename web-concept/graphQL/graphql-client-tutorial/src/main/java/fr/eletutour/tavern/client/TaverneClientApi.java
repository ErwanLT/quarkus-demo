package fr.eletutour.tavern.client;

import fr.eletutour.tavern.controller.dto.AventurierInput;
import fr.eletutour.tavern.model.Aventurier;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;

import java.util.List;

@GraphQLClientApi(configKey = "taverne")
public interface TaverneClientApi {

    List<Aventurier> aventuriers();

    Aventurier aventurier(Long id);

    Aventurier ajouterAventurier(AventurierInput input);

}