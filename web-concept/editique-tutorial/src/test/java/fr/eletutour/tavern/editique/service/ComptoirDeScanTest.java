package fr.eletutour.tavern.editique.service;

import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import fr.eletutour.tavern.editique.service.ComptoirDeScan.Resolution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests du comptoir de scan : un code-barres lu doit mener à la bonne page, ou être refusé.
 */
class ComptoirDeScanTest {

    private final ComptoirDeScan comptoir = new ComptoirDeScan(
            new ArchivesTaverne(), new IdentificationService("http://localhost:8080"));

    private static String chemin(Resolution resolution) {
        return assertInstanceOf(Resolution.Trouve.class, resolution).chemin();
    }

    private static String raison(Resolution resolution) {
        return assertInstanceOf(Resolution.Inconnu.class, resolution).raison();
    }

    @Test
    @DisplayName("Le Code 128 d'un ticket mène au suivi de la commande")
    void codeCommande() {
        assertEquals("/taverne/commandes/CMD-0042/suivi", chemin(comptoir.resoudre("CMD-0042")));
    }

    @Test
    @DisplayName("Les espaces et minuscules d'une saisie manuelle sont tolérés")
    void saisieManuelle() {
        assertEquals("/taverne/commandes/CMD-0042/suivi", chemin(comptoir.resoudre("  cmd-0042 \n")));
    }

    @Test
    @DisplayName("Un numéro de facture mène à son suivi")
    void numeroFacture() {
        assertEquals("/taverne/factures/FAC-BDF-2026-117/suivi", chemin(comptoir.resoudre("FAC-BDF-2026-117")));
    }

    @Test
    @DisplayName("L'EAN-13 d'une étiquette mène à la fiche du produit")
    void ean13Produit() {
        assertEquals("/taverne/produits/TON-ROUSSE", chemin(comptoir.resoudre("2000000000015")));
    }

    @Test
    @DisplayName("Un EAN-13 à la clé de contrôle fausse est refusé, même si sa base existe")
    void cleDeControleFausse() {
        // Base 200000000001 (Tonneau de rousse naine), mais clé 6 au lieu de 5 : chiffre mal lu
        assertTrue(raison(comptoir.resoudre("2000000000016")).contains("Clé de contrôle invalide"));
    }

    @Test
    @DisplayName("Un EAN-13 valide mais inconnu de la taverne est refusé")
    void ean13Inconnu() {
        assertTrue(raison(comptoir.resoudre("2000000000992")).contains("Aucun produit"));
    }

    @Test
    @DisplayName("Une commande inconnue et un code fantaisiste sont refusés avec une raison")
    void codesInconnus() {
        assertTrue(raison(comptoir.resoudre("CMD-9999")).contains("Aucune commande"));
        assertTrue(raison(comptoir.resoudre("DRAGON")).contains("ne ressemble"));
        assertTrue(raison(comptoir.resoudre("   ")).contains("Aucun code"));
    }
}
