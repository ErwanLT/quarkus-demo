package fr.eletutour.tavern.editique.repository;

import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.Fournisseur;
import fr.eletutour.tavern.editique.model.LigneCommande;
import fr.eletutour.tavern.editique.model.LigneFacture;
import fr.eletutour.tavern.editique.model.Paiement;
import fr.eletutour.tavern.editique.model.ReleveFournisseur;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Les archives de la taverne : commandes servies et factures des fournisseurs.
 * <p>
 * Les données sont conservées en mémoire et figées : le maître des archives
 * ne laisse personne réécrire l'histoire.
 * </p>
 */
@ApplicationScoped
public class ArchivesTaverne {

    private static final BigDecimal TVA_NORMALE = new BigDecimal("0.20");
    private static final BigDecimal TVA_REDUITE = new BigDecimal("0.055");

    /** Farines et grains livrés par la Meunerie du Val-Doré : désignation et prix unitaire HT au sac. */
    private static final String[] FARINES = {
            "Farine de blé tendre", "Farine de seigle", "Farine d'épeautre", "Farine de sarrasin",
            "Farine d'orge maltée", "Farine d'avoine", "Farine de châtaigne", "Farine de millet"
    };
    private static final String[] GRAINS = {
            "Grains de blé", "Grains d'orge", "Grains de seigle", "Grains d'avoine", "Grains de millet",
            "Grains d'épeautre", "Grains de sarrasin", "Grains de lin"
    };

    private final List<Commande> commandes;
    private final List<FactureFournisseur> factures;

    public ArchivesTaverne() {
        this.commandes = List.of(
                new Commande("CMD-0042", "Table du Dragon", "Brunehilde",
                        LocalDateTime.of(2026, 10, 25, 20, 15),
                        List.of(
                                new LigneCommande("Chope de Barbe-de-Fer", 3, new BigDecimal("4.50")),
                                new LigneCommande("Ragoût du chasseur", 2, new BigDecimal("12.00")),
                                new LigneCommande("Hydromel des Elfes", 1, new BigDecimal("7.80"))),
                        List.of(
                                new Paiement("Pièces d'or", new BigDecimal("40.00")),
                                new Paiement("Troc : dent de wyverne", new BigDecimal("5.30")))),
                new Commande("CMD-0043", "Coin du Barde", "Brunehilde",
                        LocalDateTime.of(2026, 10, 25, 21, 40),
                        List.of(new LigneCommande("Pinte de rousse", 1, new BigDecimal("5.00"))),
                        List.of(new Paiement("Pièces d'or", new BigDecimal("5.00")))));

        Fournisseur brasserie = new Fournisseur("BDF", "Brasserie Barbe-de-Fer",
                "3 galerie des Enclumes", "Khaz-Modan");
        Fournisseur meunerie = new Fournisseur("MVD", "Meunerie du Val-Doré",
                "Moulin des Trois Ruisseaux", "Val-Doré");
        Fournisseur fromagerie = new Fournisseur("FCH", "Fromagerie des Collines",
                "Trou du Fromager", "La Comté-sous-Colline");

        this.factures = List.of(
                new FactureFournisseur("FAC-BDF-2026-117", brasserie,
                        LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                        List.of(
                                new LigneFacture("TON-ROUSSE", "Tonneau de rousse naine 50L", 4, "tonneau",
                                        new BigDecimal("180.00"), TVA_NORMALE),
                                new LigneFacture("TON-BLONDE", "Tonneau de blonde des cimes 50L", 2, "tonneau",
                                        new BigDecimal("165.00"), TVA_NORMALE),
                                new LigneFacture("HOU-SAC", "Sac de houblon des montagnes", 5, "sac",
                                        new BigDecimal("22.50"), TVA_REDUITE))),
                new FactureFournisseur("FAC-MVD-2026-031", meunerie,
                        LocalDate.of(2026, 10, 5), LocalDate.of(2026, 11, 4),
                        lignesMeunerie()),
                new FactureFournisseur("FAC-FCH-2026-008", fromagerie,
                        LocalDate.of(2026, 10, 10), LocalDate.of(2026, 11, 9),
                        List.of(
                                new LigneFacture("FRO-MEULE", "Meule de tomme des collines", 2, "meule",
                                        new BigDecimal("38.00"), TVA_REDUITE),
                                new LigneFacture("FRO-CHEVRE", "Crottin de chèvre affiné", 12, "pièce",
                                        new BigDecimal("2.40"), TVA_REDUITE))),
                new FactureFournisseur("FAC-FCH-2026-012", fromagerie,
                        LocalDate.of(2026, 10, 20), LocalDate.of(2026, 11, 19),
                        List.of(
                                new LigneFacture("FRO-BLEU", "Bleu des cavernes", 3, "pièce",
                                        new BigDecimal("9.50"), TVA_REDUITE))));
    }

    /**
     * Génère les 40 lignes (FAR-01 à FAR-40) de la grosse facture de la meunerie,
     * de quoi déborder sur plusieurs pages.
     */
    private static List<LigneFacture> lignesMeunerie() {
        List<LigneFacture> lignes = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            String designation = i <= 20
                    ? FARINES[(i - 1) % FARINES.length] + " T" + (45 + 5 * ((i - 1) / FARINES.length))
                    : GRAINS[(i - 1) % GRAINS.length] + " (lot " + (i - 20) + ")";
            BigDecimal prix = BigDecimal.valueOf(1200 + 175L * i).movePointLeft(2);
            lignes.add(new LigneFacture(String.format("FAR-%02d", i), designation,
                    2 + (i % 7), "sac", prix, TVA_REDUITE));
        }
        return lignes;
    }

    public List<Commande> commandes() {
        return commandes;
    }

    public Optional<Commande> trouverCommande(String numero) {
        return commandes.stream().filter(c -> c.getNumero().equals(numero)).findFirst();
    }

    public List<FactureFournisseur> factures() {
        return factures;
    }

    public Optional<FactureFournisseur> trouverFacture(String numero) {
        return factures.stream().filter(f -> f.getNumero().equals(numero)).findFirst();
    }

    /**
     * Regroupe les factures par fournisseur, dans l'ordre d'arrivée, pour le relevé mensuel.
     */
    public List<ReleveFournisseur> releveFournisseurs() {
        return factures.stream()
                .collect(Collectors.groupingBy(f -> f.getFournisseur().getCode(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(facturesDuFournisseur -> new ReleveFournisseur(
                        facturesDuFournisseur.getFirst().getFournisseur(), facturesDuFournisseur))
                .toList();
    }
}
