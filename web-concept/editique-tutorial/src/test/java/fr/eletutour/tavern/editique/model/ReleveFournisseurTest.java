package fr.eletutour.tavern.editique.model;

import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaires du regroupement des factures par fournisseur pour le relevé mensuel.
 */
class ReleveFournisseurTest {

    private final List<ReleveFournisseur> releves = new ArchivesTaverne().releveFournisseurs();

    @Test
    @DisplayName("Le relevé regroupe les factures par fournisseur, dans l'ordre d'arrivée")
    void regroupementParFournisseur() {
        assertEquals(List.of("BDF", "MVD", "FCH"),
                releves.stream().map(r -> r.getFournisseur().getCode()).toList());
        assertEquals(List.of(1, 1, 2),
                releves.stream().map(ReleveFournisseur::getNombreFactures).toList());
    }

    @Test
    @DisplayName("Le sous-total de la Fromagerie des Collines additionne ses deux factures TTC")
    void sousTotalFromagerie() {
        ReleveFournisseur fromagerie = releves.get(2);

        // 104,80 HT -> 110,56 TTC ; 28,50 HT -> 30,07 TTC
        assertEquals(0, new BigDecimal("140.63").compareTo(fromagerie.getTotalTtc()));
    }
}
