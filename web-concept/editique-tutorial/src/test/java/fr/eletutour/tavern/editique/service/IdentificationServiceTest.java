package fr.eletutour.tavern.editique.service;

import fr.eletutour.tavern.editique.model.CarteTable;
import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.EtiquetteProduit;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.Fournisseur;
import fr.eletutour.tavern.editique.model.Produit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests du service qui décide de ce qui part dans les codes.
 */
class IdentificationServiceTest {

    private final IdentificationService identification =
            new IdentificationService("https://taverne.example/");

    @Test
    @DisplayName("Le Code 128 du ticket encode le numéro de commande")
    void codeCommande() {
        Commande commande = new Commande("CMD-0042", "Table du Dragon", "Brunehilde",
                LocalDateTime.of(2026, 10, 25, 20, 15), List.of());

        assertEquals("CMD-0042", identification.codeCommande(commande));
    }

    @Test
    @DisplayName("La clé de contrôle EAN-13 est calculée à partir des 12 premiers chiffres")
    void cleEan13() {
        assertEquals("3570000000012", identification.codeEan13("357000000001"));
        assertEquals("3570000000029", identification.codeEan13("357000000002"));
        assertEquals("9780201379624", identification.codeEan13("978020137962"));
    }

    @Test
    @DisplayName("Un EAN-13 de base invalide est refusé")
    void ean13Invalide() {
        assertThrows(IllegalArgumentException.class, () -> identification.codeEan13("35700000000"));
        assertThrows(IllegalArgumentException.class, () -> identification.codeEan13("35700000000A"));
        assertThrows(IllegalArgumentException.class, () -> identification.codeEan13(null));
    }

    @Test
    @DisplayName("L'étiquette reçoit l'EAN-13 complet, clé comprise")
    void etiquette() {
        Produit produit = new Produit("TON-ROUSSE", "Tonneau de rousse naine 50L",
                new BigDecimal("180.00"), "tonneau", "357000000001");

        EtiquetteProduit etiquette = identification.etiquette(produit);

        assertEquals("3570000000012", etiquette.getEan13());
        assertEquals("TON-ROUSSE", etiquette.getReference());
    }

    @Test
    @DisplayName("Les URL de suivi pointent vers les routes REST, sans double slash")
    void urlsDeSuivi() {
        Commande commande = new Commande("CMD-0042", "Table du Dragon", "Brunehilde",
                LocalDateTime.of(2026, 10, 25, 20, 15), List.of());
        FactureFournisseur facture = new FactureFournisseur("FAC-BDF-2026-117",
                new Fournisseur("BDF", "Brasserie Barbe-de-Fer", "3 galerie des Enclumes", "Khaz-Modan"),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), List.of());

        assertEquals("https://taverne.example/taverne/commandes/CMD-0042/suivi",
                identification.urlCommande(commande));
        assertEquals("https://taverne.example/taverne/factures/FAC-BDF-2026-117/suivi",
                identification.urlFacture(facture));
    }

    @Test
    @DisplayName("Le QR d'une carte de table encode le nom de la table dans l'URL du menu")
    void carteTable() {
        CarteTable carte = identification.carteTable("Table du Dragon");

        assertEquals("Table du Dragon", carte.getNom());
        assertEquals("https://taverne.example/taverne/menu?table=Table%20du%20Dragon", carte.getUrl());
    }
}
