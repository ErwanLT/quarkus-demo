package fr.eletutour.tavern.versioning.service;

import fr.eletutour.tavern.versioning.dto.MenuResponse;
import fr.eletutour.tavern.versioning.dto.MenuV1Response;
import fr.eletutour.tavern.versioning.dto.MenuV2Response;
import fr.eletutour.tavern.versioning.dto.PriceResponse;
import fr.eletutour.tavern.versioning.model.MenuVersion;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TavernMenuService {

    public MenuV1Response serveHistoricalMenu() {
        return new MenuV1Response("Ragout de sanglier", 12);
    }

    public MenuV2Response serveGuildMenu() {
        return new MenuV2Response(
                "Ragout de sanglier aux herbes de druide",
                new PriceResponse(12, "pieces-cuivre"),
                List.of("sanglier", "orge", "carottes", "thym de druide"),
                "aventuriers niveau 2 et plus"
        );
    }

    public MenuResponse serveMenuForVersion(String requestedVersion) {
        return switch (MenuVersion.fromApiValue(requestedVersion)) {
            case V1 -> serveHistoricalMenu();
            case V2 -> serveGuildMenu();
        };
    }
}
