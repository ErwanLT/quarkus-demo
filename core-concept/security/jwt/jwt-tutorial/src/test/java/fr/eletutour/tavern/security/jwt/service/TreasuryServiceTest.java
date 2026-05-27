package fr.eletutour.tavern.security.jwt.service;

import fr.eletutour.tavern.security.jwt.dto.LedgerResponse;
import fr.eletutour.tavern.security.jwt.dto.VaultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreasuryServiceTest {

    private TreasuryService treasuryService;

    @BeforeEach
    void setUp() {
        treasuryService = new TreasuryService();
    }

    @Test
    @DisplayName("Le livre de comptes doit contenir les entrees attendues")
    void testLedger() {
        LedgerResponse response = treasuryService.ledger();

        assertEquals("grand-ledger", response.book());
        assertFalse(response.entries().isEmpty());
        assertTrue(response.entries().stream().anyMatch(e -> e.contains("Ale festival")));
    }

    @Test
    @DisplayName("Le coffre doit contenir les biens attendus et etre scelle")
    void testVault() {
        VaultResponse response = treasuryService.vault();

        assertEquals("iron-vault", response.vault());
        assertEquals("sealed", response.status());
        assertTrue(response.assets().contains("Emerald chalice"));
    }
}
