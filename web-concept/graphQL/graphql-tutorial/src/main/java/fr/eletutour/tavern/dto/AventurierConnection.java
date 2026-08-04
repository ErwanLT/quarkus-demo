package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

@Description("Une page du registre des aventuriers")
public record AventurierConnection(
        @NonNull List<@NonNull AventurierEdge> edges,
        @NonNull PageInfo pageInfo) {
}