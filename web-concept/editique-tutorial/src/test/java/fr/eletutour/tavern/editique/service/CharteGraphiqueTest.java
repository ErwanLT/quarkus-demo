package fr.eletutour.tavern.editique.service;

import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * La charte graphique vérifiée sur les documents imprimés : on relit les couleurs
 * réellement posées dans le PDF (opérateurs de couleur du flux de contenu).
 */
@QuarkusTest
class CharteGraphiqueTest {

    /** La palette de charte-taverne.jrtx, plus le blanc du papier. */
    private static final Set<String> PALETTE = Set.of(
            "#2B2118", // encre
            "#5A4630", // sépia
            "#4A3720", // brou de noix
            "#EADFC0", // parchemin
            "#FBF7EC", // vélin
            "#F5EEDB", // vélin ombré
            "#BFB59B", // filet clair
            "#000000", // codes-barres
            "#FFFFFF"  // papier
    );

    @Inject
    ImprimerieService imprimerie;

    @Inject
    ArchivesTaverne archives;

    @Test
    @DisplayName("Facture, relevé, étiquettes et cartes de table n'emploient que les couleurs de la charte")
    void documentsA4DansLaPalette() throws IOException {
        Map<String, byte[]> documents = Map.of(
                "facture", imprimerie.imprimerFacture(facture("FAC-MVD-2026-031")),
                "relevé", imprimerie.imprimerReleve(archives.releveFournisseurs(), "Octobre 2026"),
                "étiquettes", imprimerie.imprimerEtiquettes(archives.produits()),
                "cartes", imprimerie.imprimerCartesTables(List.of("Table du Dragon", "Comptoir")));

        documents.forEach((nom, pdf) -> {
            Set<String> horsCharte = new TreeSet<>(couleurs(pdf));
            horsCharte.removeAll(PALETTE);
            assertTrue(horsCharte.isEmpty(), () -> nom + " : couleurs hors charte " + horsCharte);
        });
    }

    /**
     * Le garde-fou de la charte thermique : redéfinir « Base » ne suffit pas à repeindre
     * les styles hérités de la charte de la taverne (voir charte-thermique.jrtx).
     */
    @Test
    @DisplayName("Le ticket thermique ne contient pas une goutte de couleur : uniquement du noir et du blanc")
    void ticketEnNoirEtBlanc() {
        Set<String> couleurs = couleurs(imprimerie.imprimerTicket(commande("CMD-0042")));
        assertFalse(couleurs.isEmpty(), "aucune couleur relevée dans le ticket");
        assertTrue(Set.of("#000000", "#FFFFFF").containsAll(couleurs), () -> "couleurs sur le ticket : " + couleurs);
    }

    @Test
    @DisplayName("Les lignes de la facture sont ombrées une sur deux par le style conditionnel de la charte")
    void lignesZebrees() {
        assertTrue(couleurs(imprimerie.imprimerFacture(facture("FAC-MVD-2026-031"))).contains("#F5EEDB"),
                "aucune ligne ombrée sur la facture de la meunerie");
    }

    /** Couleurs de remplissage et de trait posées dans un PDF, en notation #RRGGBB. */
    private static Set<String> couleurs(byte[] pdf) {
        Set<String> couleurs = new TreeSet<>();
        try (PDDocument document = Loader.loadPDF(pdf)) {
            for (PDPage page : document.getPages()) {
                relever(new PDFStreamParser(page).parse(), couleurs);
            }
        } catch (IOException e) {
            throw new IllegalStateException("PDF illisible", e);
        }
        return couleurs;
    }

    private static void relever(List<Object> jetons, Set<String> couleurs) {
        List<Float> operandes = new ArrayList<>();
        for (Object jeton : jetons) {
            if (jeton instanceof COSNumber nombre) {
                operandes.add(nombre.floatValue());
            } else if (jeton instanceof Operator operateur) {
                String code = operateur.getName();
                if ((code.equals("rg") || code.equals("RG")) && operandes.size() >= 3) {
                    int n = operandes.size();
                    couleurs.add(hex(operandes.get(n - 3), operandes.get(n - 2), operandes.get(n - 1)));
                } else if ((code.equals("g") || code.equals("G")) && !operandes.isEmpty()) {
                    float gris = operandes.getLast();
                    couleurs.add(hex(gris, gris, gris));
                }
                operandes.clear();
            } else if (jeton instanceof COSBase) {
                operandes.clear();
            }
        }
    }

    private static String hex(float r, float g, float b) {
        return String.format("#%02X%02X%02X", Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
    }

    private FactureFournisseur facture(String numero) {
        return archives.trouverFacture(numero).orElseThrow();
    }

    private Commande commande(String numero) {
        return archives.trouverCommande(numero).orElseThrow();
    }
}
