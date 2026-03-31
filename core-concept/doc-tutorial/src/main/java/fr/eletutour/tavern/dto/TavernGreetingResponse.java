package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Message d'accueil de la taverne")
public record TavernGreetingResponse(
        @Schema(description = "Message retourne")
        String message
) {
}
