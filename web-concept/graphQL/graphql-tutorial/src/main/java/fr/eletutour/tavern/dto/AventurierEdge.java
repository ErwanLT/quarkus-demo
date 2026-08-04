package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.NonNull;
 
@Description("Un aventurier accompagné de son curseur de lecture")
public record AventurierEdge(
    @NonNull String cursor,
    @NonNull AventurierResponse node) {
}