package fr.eletutour.tavern.editique.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires de la facture fournisseur : calculs HT, TVA et TTC.
 */
class FactureFournisseurTest {

    private static final BigDecimal TVA_NORMALE = new BigDecimal("0.20");
    private static final BigDecimal TVA_REDUITE = new BigDecimal("0.055");

    /**
     * Reconstitue la facture FAC-BDF-2026-117 de la Brasserie Barbe-de-Fer.
     */
    private static FactureFournisseur factureBarbeDeFer() {
        Fournisseur brasserie = new Fournisseur("BDF", "Brasserie Barbe-de-Fer",
                "3 galerie des Enclumes", "Khaz-Modan");
        return new FactureFournisseur("FAC-BDF-2026-117", brasserie,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                List.of(
                        new LigneFacture("TON-ROUSSE", "Tonneau de rousse naine 50L", 4, "tonneau",
                                new BigDecimal("180.00"), TVA_NORMALE),
                        new LigneFacture("TON-BLONDE", "Tonneau de blonde des cimes 50L", 2, "tonneau",
                                new BigDecimal("165.00"), TVA_NORMALE),
                        new LigneFacture("HOU-SAC", "Sac de houblon des montagnes", 5, "sac",
                                new BigDecimal("22.50"), TVA_REDUITE)));
    }

    private static void assertMontant(String attendu, BigDecimal obtenu) {
        assertEquals(0, new BigDecimal(attendu).compareTo(obtenu),
                () -> "attendu " + attendu + " pièces d'or mais obtenu " + obtenu);
    }

    @Test
    @DisplayName("Le total HT de la facture Barbe-de-Fer vaut 1162,50 pièces d'or")
    void totalHt() {
        assertMontant("1162.50", factureBarbeDeFer().getTotalHt());
    }

    @Test
    @DisplayName("Le récapitulatif de TVA regroupe les lignes par taux, trié par taux croissant")
    void recapTvaTrieParTaux() {
        List<LigneTva> recap = factureBarbeDeFer().getRecapTva();

        assertEquals(2, recap.size());

        // Le houblon au taux réduit passe en premier
        LigneTva reduite = recap.get(0);
        assertEquals(0, TVA_REDUITE.compareTo(reduite.getTaux()));
        assertMontant("112.50", reduite.getBaseHt());
        assertMontant("6.19", reduite.getMontantTva());

        // Puis les tonneaux au taux normal
        LigneTva normale = recap.get(1);
        assertEquals(0, TVA_NORMALE.compareTo(normale.getTaux()));
        assertMontant("1050.00", normale.getBaseHt());
        assertMontant("210.00", normale.getMontantTva());
    }

    @Test
    @DisplayName("Le total de TVA vaut 216,19 pièces d'or")
    void totalTva() {
        assertMontant("216.19", factureBarbeDeFer().getTotalTva());
    }

    @Test
    @DisplayName("Le total TTC vaut 1378,69 pièces d'or")
    void totalTtc() {
        assertMontant("1378.69", factureBarbeDeFer().getTotalTtc());
    }

    @Test
    @DisplayName("Les lignes de la facture sont une copie immuable de celles fournies")
    void lignesImmuables() {
        List<LigneFacture> lignes = new ArrayList<>(factureBarbeDeFer().getLignes());
        FactureFournisseur facture = new FactureFournisseur("FAC-TEST", null,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), lignes);

        // Un fournisseur malicieux ne peut pas glisser un tonneau de plus après coup...
        lignes.add(new LigneFacture("TON-PIRATE", "Tonneau fantôme", 1, "tonneau",
                new BigDecimal("999.00"), TVA_NORMALE));
        assertEquals(3, facture.getLignes().size());

        // ... ni en passant par la facture elle-même
        assertThrows(UnsupportedOperationException.class, () -> facture.getLignes().clear());
    }
}
