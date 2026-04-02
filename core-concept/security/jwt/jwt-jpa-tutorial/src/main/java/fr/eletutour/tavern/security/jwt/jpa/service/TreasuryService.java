package fr.eletutour.tavern.security.jwt.jpa.service;

import fr.eletutour.tavern.security.jwt.jpa.dto.LedgerResponse;
import fr.eletutour.tavern.security.jwt.jpa.dto.VaultResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TreasuryService {

    public LedgerResponse ledger() {
        return new LedgerResponse("grand-ledger", List.of(
                "Ale festival - 12 silver",
                "Mead barrels - 7 silver",
                "Royal tax - 3 silver"
        ));
    }

    public VaultResponse vault() {
        return new VaultResponse("iron-vault", "sealed", List.of(
                "Emerald chalice",
                "Guild charter",
                "Emergency gold stash"
        ));
    }
}
