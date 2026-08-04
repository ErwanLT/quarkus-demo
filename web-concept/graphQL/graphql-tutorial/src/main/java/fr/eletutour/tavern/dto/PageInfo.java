package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Description;
 
@Description("Métadonnées de pagination")
public record PageInfo(
    @NonNull Boolean hasNextPage,
    String endCursor) {
}