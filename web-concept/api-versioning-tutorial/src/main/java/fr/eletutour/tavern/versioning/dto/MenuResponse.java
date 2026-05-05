package fr.eletutour.tavern.versioning.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Famille fermee des representations versionnees du menu de la taverne.")
public sealed interface MenuResponse permits MenuV1Response, MenuV2Response {

    @Schema(examples = "Ragout de sanglier")
    String plat();
}
