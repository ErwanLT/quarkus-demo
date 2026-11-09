package fr.eletutour.tavern.editique.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.Fournisseur;
import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'intégration de l'imprimerie de la taverne : on imprime de vrais documents
 * avec JasperReports, puis on relit le PDF produit avec PDFBox.
 */
@QuarkusTest
class ImprimerieServiceTest {

    @Inject
    ImprimerieService imprimerie;

    @Inject
    IdentificationService identification;

    @Inject
    ArchivesTaverne archives;

    /**
     * Contenu textuel et nombre de pages d'un PDF imprimé.
     */
    private record PdfLu(String texte, int pages) {
    }

    /**
     * Relit un PDF avec PDFBox. La locale FR produit des espaces insécables
     * (U+00A0, U+202F) comme séparateurs de milliers ou avant « % » : on les
     * remplace par un espace simple pour pouvoir écrire des assertions lisibles.
     */
    private static PdfLu lirePdf(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            String texte = new PDFTextStripper().getText(document)
                    .replace(' ', ' ')
                    .replace(' ', ' ');
            return new PdfLu(texte, document.getNumberOfPages());
        }
    }

    /**
     * Texte d'une seule page du PDF (numérotée à partir de 1), espaces insécables normalisés.
     */
    private static String textePage(byte[] pdf, int page) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            PDFTextStripper extracteur = new PDFTextStripper();
            extracteur.setStartPage(page);
            extracteur.setEndPage(page);
            return extracteur.getText(document)
                    .replace('\u00A0', ' ')
                    .replace('\u202F', ' ');
        }
    }

    /**
     * Dimensions de la première page d'un PDF.
     */
    private static PDRectangle formatPremierePage(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return document.getPage(0).getMediaBox();
        }
    }

    /**
     * « Scanne » une page du PDF à plusieurs résolutions : on la rasterise à 200
     * puis 300 dpi, et ZXing cherche tous les codes-barres et QR codes qu'elle
     * contient. Les codes 1D fins se lisent mieux à 300 dpi, tandis que les QR
     * codes denses rendus en vectoriel par PDFBox passent mieux à 200 dpi.
     */
    private static List<Result> decoderCodes(byte[] pdf, int page, BarcodeFormat... formats) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            Map<String, Result> trouves = new LinkedHashMap<>();
            for (int dpi : new int[]{200, 300}) {
                BufferedImage image = new PDFRenderer(document).renderImageWithDPI(page - 1, dpi);
                BinaryBitmap bitmap = new BinaryBitmap(
                        new HybridBinarizer(new BufferedImageLuminanceSource(image)));
                Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
                hints.put(DecodeHintType.POSSIBLE_FORMATS, List.of(formats));
                hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                MultiFormatReader lecteur = new MultiFormatReader();
                lecteur.setHints(hints);
                MultipleBarcodeReader multi = new GenericMultipleBarcodeReader(lecteur);
                try {
                    for (Result resultat : multi.decodeMultiple(bitmap, hints)) {
                        trouves.putIfAbsent(resultat.getBarcodeFormat() + "\0" + resultat.getText(), resultat);
                    }
                } catch (com.google.zxing.NotFoundException ignored) {
                    // Aucun code lisible à cette résolution : on tente la suivante.
                }
            }
            return new ArrayList<>(trouves.values());
        }
    }

    /**
     * Décode un code par zone : la page rendue est découpée en {@code colonnes} x {@code rangees}
     * tuiles, chacune lue séparément. Sur une planche de six QR codes, le lecteur multiple de ZXing
     * peut en manquer un ; une tuile ne contient qu'un seul code, la lecture est alors fiable.
     */
    private static List<Result> decoderParZones(byte[] pdf, int page, int dpi, int colonnes, int rangees,
                                                BarcodeFormat format) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            BufferedImage image = new PDFRenderer(document).renderImageWithDPI(page - 1, dpi);
            int largeur = image.getWidth() / colonnes;
            int hauteur = image.getHeight() / rangees;
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, List.of(format));
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            List<Result> resultats = new ArrayList<>();
            for (int rangee = 0; rangee < rangees; rangee++) {
                for (int colonne = 0; colonne < colonnes; colonne++) {
                    BufferedImage tuile = image.getSubimage(colonne * largeur, rangee * hauteur, largeur, hauteur);
                    BinaryBitmap bitmap = new BinaryBitmap(
                            new HybridBinarizer(new BufferedImageLuminanceSource(tuile)));
                    try {
                        resultats.add(new MultiFormatReader().decode(bitmap, hints));
                    } catch (com.google.zxing.NotFoundException ignored) {
                        // Tuile sans code lisible : l'assertion appelante le signalera.
                    }
                }
            }
            return resultats;
        }
    }

    private static List<String> contenus(List<Result> resultats, BarcodeFormat format) {
        return resultats.stream()
                .filter(resultat -> resultat.getBarcodeFormat() == format)
                .map(Result::getText)
                .toList();
    }

    private static void assertContientCode(List<Result> resultats, BarcodeFormat format, String attendu) {
        List<String> contenus = contenus(resultats, format);
        assertTrue(contenus.contains(attendu),
                () -> "code " + format + " « " + attendu + " » introuvable, lus : " + contenus);
    }

    private static void assertCommencePar(String signature, byte[] contenu) {
        assertTrue(contenu.length > signature.length(), "le document imprimé est vide");
        String entete = new String(contenu, 0, signature.length(), StandardCharsets.ISO_8859_1);
        assertEquals(signature, entete);
    }

    private static void assertContient(String texte, String... attendus) {
        for (String attendu : attendus) {
            assertTrue(texte.contains(attendu),
                    () -> "« " + attendu + " » introuvable dans le document :\n" + texte);
        }
    }

    private Commande commande(String numero) {
        return archives.trouverCommande(numero).orElseThrow();
    }

    private FactureFournisseur facture(String numero) {
        return archives.trouverFacture(numero).orElseThrow();
    }

    @Test
    @DisplayName("Le ticket de caisse de la Table du Dragon est un PDF complet tenant sur une seule page")
    void ticketDeCaisse() throws IOException {
        byte[] pdf = imprimerie.imprimerTicket(commande("CMD-0042"));

        assertCommencePar("%PDF", pdf);
        PdfLu ticket = lirePdf(pdf);
        assertEquals(1, ticket.pages(), "un ticket de caisse est un rouleau d'une seule page");
        assertContient(ticket.texte(),
                "The Falling Whale",
                "CMD-0042",
                "Table du Dragon",
                "Hydromel des Elfes",
                "TOTAL",
                "45,30",
                "Merci de votre visite, aventurier !");
    }

    @Test
    @DisplayName("Le composant list du ticket détaille chaque règlement, troc compris")
    void ticketDetailleLesPaiements() throws IOException {
        PdfLu ticket = lirePdf(imprimerie.imprimerTicket(commande("CMD-0042")));

        assertContient(ticket.texte(),
                "Règlement :",
                "Pièces d'or 40,00 po",
                "Troc : dent de wyverne 5,30 po");
    }

    @Test
    @DisplayName("La facture de la Brasserie Barbe-de-Fer affiche fournisseur, dates, TVA et totaux")
    void factureBarbeDeFer() throws IOException {
        byte[] pdf = imprimerie.imprimerFacture(facture("FAC-BDF-2026-117"));

        assertCommencePar("%PDF", pdf);
        PdfLu facture = lirePdf(pdf);
        assertContient(facture.texte(),
                "FACTURE",
                "Brasserie Barbe-de-Fer",
                "01/10/2026",
                "TON-ROUSSE",
                "5,5 %",
                "1 162,50",
                "1 378,69",
                "Page 1 / 1");
    }

    @Test
    @DisplayName("Le sous-rapport TVA reçoit le numéro de facture et le sous-rapport des lignes renvoie son compte")
    void sousRapportsParametreEtValeurDeRetour() throws IOException {
        PdfLu facture = lirePdf(imprimerie.imprimerFacture(facture("FAC-BDF-2026-117")));

        assertContient(facture.texte(),
                "Détail de la TVA de la facture FAC-BDF-2026-117",
                "Lignes facturées : 3");
    }

    @Test
    @DisplayName("Le relevé d'octobre liste chaque fournisseur, ses factures et le total général")
    void releveDesFournisseurs() throws IOException {
        byte[] pdf = imprimerie.imprimerReleve(archives.releveFournisseurs(), "Octobre 2026");

        assertCommencePar("%PDF", pdf);
        PdfLu releve = lirePdf(pdf);
        assertContient(releve.texte(),
                "RELEVÉ DES FOURNISSEURS",
                "Octobre 2026",
                "Brasserie Barbe-de-Fer",
                "Fromagerie des Collines",
                "2 factures",
                "FAC-FCH-2026-008",
                "FAC-FCH-2026-012",
                "TOTAL GÉNÉRAL (4 factures)",
                "11 704,03");
    }

    @Test
    @DisplayName("La facture de la Meunerie du Val-Doré, avec ses 40 lignes, s'étale sur plusieurs pages")
    void factureMultipages() throws IOException {
        byte[] pdf = imprimerie.imprimerFacture(facture("FAC-MVD-2026-031"));

        assertCommencePar("%PDF", pdf);
        PdfLu facture = lirePdf(pdf);
        assertTrue(facture.pages() >= 2,
                () -> "40 lignes de farine devraient déborder sur une 2e page, pages = " + facture.pages());
        assertContient(facture.texte(), "FAR-40", "Page 2 / ");
    }

    @Test
    @DisplayName("L'export tableur de la facture est un classeur XLSX (archive ZIP)")
    void exportXlsx() {
        byte[] xlsx = imprimerie.exporterFactureXlsx(facture("FAC-BDF-2026-117"));

        assertCommencePar("PK", xlsx);
    }

    @Test
    @DisplayName("Le classeur XLSX s'ouvre avec Apache POI et contient le numéro de la facture")
    void exportXlsxLisible() throws IOException {
        byte[] xlsx = imprimerie.exporterFactureXlsx(facture("FAC-BDF-2026-117"));

        boolean trouve = false;
        try (Workbook classeur = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            for (Sheet feuille : classeur) {
                for (Row ligne : feuille) {
                    for (Cell cellule : ligne) {
                        if (cellule.getCellType() == CellType.STRING
                                && cellule.getStringCellValue().contains("FAC-BDF-2026-117")) {
                            trouve = true;
                        }
                    }
                }
            }
        }
        assertTrue(trouve, "aucune cellule du classeur ne mentionne la facture FAC-BDF-2026-117");
    }

    @Test
    @DisplayName("Le rouleau du ticket (ignorePagination) a la hauteur de son contenu, pas celle d'une page A4")
    void hauteurDuTicketSuitLeContenu() throws IOException {
        PDRectangle dragon = formatPremierePage(imprimerie.imprimerTicket(commande("CMD-0042")));
        PDRectangle autre = formatPremierePage(imprimerie.imprimerTicket(commande("CMD-0043")));

        assertEquals(226f, dragon.getWidth(), 0.5f, "le rouleau thermique fait 80 mm de large");
        assertEquals(226f, autre.getWidth(), 0.5f, "le rouleau thermique fait 80 mm de large");
        assertTrue(dragon.getHeight() > autre.getHeight(),
                () -> "CMD-0042 a plus de lignes que CMD-0043 : " + dragon.getHeight() + " <= " + autre.getHeight());
        assertTrue(dragon.getHeight() < 842f, () -> "ticket CMD-0042 haut de " + dragon.getHeight() + " pt");
        assertTrue(autre.getHeight() < 842f, () -> "ticket CMD-0043 haut de " + autre.getHeight() + " pt");
    }

    @Test
    @DisplayName("Chaque page de la facture de la meunerie porte « Page n / total » et l'en-tête des colonnes")
    void paginationExacteDeLaMeunerie() throws IOException {
        byte[] pdf = imprimerie.imprimerFacture(facture("FAC-MVD-2026-031"));
        int total = lirePdf(pdf).pages();
        assertEquals(2, total, "40 lignes de farine tiennent sur deux pages");

        for (int page = 1; page <= total; page++) {
            assertContient(textePage(pdf, page), "Page " + page + " / " + total, "Désignation");
        }
    }

    @Test
    @DisplayName("Une facture sans aucune ligne s'imprime quand même et annonce 0 ligne facturée")
    void factureVide() throws IOException {
        FactureFournisseur vide = new FactureFournisseur("FAC-VIDE-2026-000",
                new Fournisseur("VIDE", "Colporteur Fantôme", "Chemin des Brumes", "Nulle-Part"),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), List.of());

        byte[] pdf = assertDoesNotThrow(() -> imprimerie.imprimerFacture(vide));

        assertCommencePar("%PDF", pdf);
        String texte = lirePdf(pdf).texte();
        assertContient(texte, "FAC-VIDE-2026-000", "Lignes facturées : 0", "Page 1 / 1");
        assertFalse(texte.contains("null"), () -> "« null » imprimé sur la facture vide :\n" + texte);
    }

    @Test
    @DisplayName("Toutes les polices de la facture sont des DejaVu embarquées dans le PDF")
    void policesEmbarquees() throws IOException {
        byte[] pdf = imprimerie.imprimerFacture(facture("FAC-BDF-2026-117"));

        List<String> polices = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(pdf)) {
            for (PDPage page : document.getPages()) {
                PDResources ressources = page.getResources();
                for (COSName nom : ressources.getFontNames()) {
                    PDFont police = ressources.getFont(nom);
                    polices.add(police.getName());
                    assertTrue(police.isEmbedded(), () -> "police non embarquée : " + police.getName());
                    assertTrue(police.getName().contains("DejaVu"),
                            () -> "police inattendue : " + police.getName());
                }
            }
        }
        assertFalse(polices.isEmpty(), "aucune police trouvée dans le PDF");
    }

    /**
     * Compte les logos posés sur la première page d'un PDF. Les codes-barres étant eux aussi
     * dessinés en images matricielles, on reconnaît le logo d'impression à ses dimensions (260 x 258 px).
     */
    private static int logosPremierePage(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            PDResources ressources = document.getPage(0).getResources();
            int logos = 0;
            for (COSName nom : ressources.getXObjectNames()) {
                if (ressources.isImageXObject(nom)
                        && ressources.getXObject(nom) instanceof PDImageXObject image
                        && image.getWidth() == 260 && image.getHeight() == 258) {
                    logos++;
                }
            }
            return logos;
        }
    }

    @Test
    @DisplayName("Le logo de The Falling Whale orne le ticket, la facture et le relevé")
    void logoSurChaqueDocument() throws IOException {
        // Ticket : image résolue par son chemin sur le classpath
        assertEquals(1, logosPremierePage(imprimerie.imprimerTicket(commande("CMD-0042"))));
        // Facture et relevé : image transmise en paramètre par le service
        assertEquals(1, logosPremierePage(imprimerie.imprimerFacture(facture("FAC-BDF-2026-117"))));
        assertEquals(1, logosPremierePage(imprimerie.imprimerReleve(archives.releveFournisseurs(), "Octobre 2026")));
    }

    @Test
    @DisplayName("Avec le logo d'impression, un ticket reste léger (moins de 150 Ko)")
    void ticketLeger() {
        // Avec le logo d'origine (PNG de 736 Ko), le ticket dépassait les 700 Ko
        byte[] pdf = imprimerie.imprimerTicket(commande("CMD-0042"));

        assertTrue(pdf.length < 150 * 1024, () -> "ticket trop lourd : " + pdf.length / 1024 + " Ko");
    }

    @Test
    @DisplayName("Le ticket porte un Code 128 (numéro de commande) et un QR code (URL de suivi)")
    void codesDuTicket() throws IOException {
        byte[] pdf = imprimerie.imprimerTicket(commande("CMD-0042"));

        List<Result> codes = decoderCodes(pdf, 1, BarcodeFormat.CODE_128, BarcodeFormat.QR_CODE);

        assertContientCode(codes, BarcodeFormat.CODE_128, "CMD-0042");
        assertContientCode(codes, BarcodeFormat.QR_CODE,
                "http://localhost:8080/taverne/commandes/CMD-0042/suivi");
    }

    @Test
    @DisplayName("La facture porte un QR code qui ramène vers sa page de suivi")
    void qrCodeDeLaFacture() throws IOException {
        byte[] pdf = imprimerie.imprimerFacture(facture("FAC-BDF-2026-117"));

        List<Result> codes = decoderCodes(pdf, 1, BarcodeFormat.QR_CODE);

        assertContientCode(codes, BarcodeFormat.QR_CODE,
                "http://localhost:8080/taverne/factures/FAC-BDF-2026-117/suivi");
    }

    @Test
    @DisplayName("Chaque étiquette produit porte un EAN-13 valide et lisible après impression")
    void etiquettesProduits() throws IOException {
        byte[] pdf = imprimerie.imprimerEtiquettes(archives.produits());

        List<Result> codes = decoderCodes(pdf, 1, BarcodeFormat.EAN_13);

        List<String> attendus = archives.produits().stream()
                .map(produit -> identification.codeEan13(produit.getCodeEan13Base()))
                .toList();
        for (String ean13 : attendus) {
            assertContientCode(codes, BarcodeFormat.EAN_13, ean13);
        }
    }

    @Test
    @DisplayName("Chaque carte de table porte un QR code vers le menu avec sa table en contexte")
    void qrCodesDesCartesTables() throws IOException {
        byte[] pdf = imprimerie.imprimerCartesTables(archives.tables());

        // Planche de 2 colonnes x 3 rangées : une carte, donc un QR code, par tuile.
        // Lecture à 300 dpi, la résolution d'impression : c'est là que les QR dessinés
        // en SVG (un rectangle par module) devenaient illisibles.
        List<Result> codes = decoderParZones(pdf, 1, 300, 2, 3, BarcodeFormat.QR_CODE);

        for (String table : archives.tables()) {
            String url = "http://localhost:8080/taverne/menu?table="
                    + table.replace(" ", "%20");
            assertContientCode(codes, BarcodeFormat.QR_CODE, url);
        }
    }
}
