package fr.eletutour.tavern.editique.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires de la commande : l'ardoise d'une table de la taverne.
 */
class CommandeTest {

    /**
     * Reconstitue la commande CMD-0042 de la Table du Dragon, telle que figée dans les archives.
     */
    private static Commande commandeTableDuDragon() {
        return new Commande("CMD-0042", "Table du Dragon", "Brunehilde",
                LocalDateTime.of(2026, 10, 25, 20, 15),
                List.of(
                        new LigneCommande("Chope de Barbe-de-Fer", 3, new BigDecimal("4.50")),
                        new LigneCommande("Ragoût du chasseur", 2, new BigDecimal("12.00")),
                        new LigneCommande("Hydromel des Elfes", 1, new BigDecimal("7.80"))));
    }

    @Test
    @DisplayName("Le montant d'une ligne vaut quantité × prix unitaire")
    void montantDUneLigne() {
        LigneCommande chopes = new LigneCommande("Chope de Barbe-de-Fer", 3, new BigDecimal("4.50"));

        assertEquals(0, new BigDecimal("13.50").compareTo(chopes.getMontant()));
    }

    @Test
    @DisplayName("Le total de la commande CMD-0042 vaut 45,30 pièces d'or")
    void totalDeLaCommande() {
        assertEquals(0, new BigDecimal("45.30").compareTo(commandeTableDuDragon().getTotal()));
    }

    @Test
    @DisplayName("Une commande sans consommation coûte 0 pièce d'or")
    void commandeVide() {
        Commande ardoiseVierge = new Commande("CMD-0000", "Coin du Barde", "Brunehilde",
                LocalDateTime.of(2026, 10, 25, 18, 0), List.of());

        assertEquals(0, BigDecimal.ZERO.compareTo(ardoiseVierge.getTotal()));
        assertEquals(List.of(), ardoiseVierge.getPaiements());
    }

    @Test
    @DisplayName("Les lignes d'une commande ne peuvent pas être modifiées après coup")
    void lignesImmuables() {
        Commande commande = commandeTableDuDragon();

        assertThrows(UnsupportedOperationException.class,
                () -> commande.getLignes().add(new LigneCommande("Pinte gratuite", 1, BigDecimal.ZERO)));
    }
}
