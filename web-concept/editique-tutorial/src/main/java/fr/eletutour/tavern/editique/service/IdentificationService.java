package fr.eletutour.tavern.editique.service;

import fr.eletutour.tavern.editique.model.CarteTable;
import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.EtiquetteProduit;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.Produit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Décide de ce qui sera encodé dans les codes-barres et les QR codes.
 * <p>
 * Le rapport JasperReports ne construit aucune URL et ne calcule aucune clé :
 * il reçoit des chaînes prêtes à dessiner. Toute la logique métier reste ici.
 * </p>
 */
@ApplicationScoped
public class IdentificationService {

    private final String urlPublique;

    @Inject
    public IdentificationService(
            @ConfigProperty(name = "taverne.url-publique", defaultValue = "http://localhost:8080")
            String urlPublique) {
        this.urlPublique = urlPublique.endsWith("/")
                ? urlPublique.substring(0, urlPublique.length() - 1)
                : urlPublique;
    }

    /**
     * Ce qui part dans le Code 128 du ticket : le numéro de commande, lisible par l'humain aussi.
     */
    public String codeCommande(Commande commande) {
        return commande.getNumero();
    }

    /**
     * Ce qui part dans le Code 128 d'une étiquette : la référence interne du produit.
     */
    public String codeProduit(Produit produit) {
        return produit.getReference();
    }

    /**
     * URL de suivi d'une commande, encodée dans le QR code du ticket.
     */
    public String urlCommande(Commande commande) {
        return urlPublique + "/taverne/commandes/" + segment(commande.getNumero()) + "/suivi";
    }

    /**
     * URL de suivi d'une facture fournisseur, encodée dans le QR code de la facture.
     */
    public String urlFacture(FactureFournisseur facture) {
        return urlPublique + "/taverne/factures/" + segment(facture.getNumero()) + "/suivi";
    }

    /**
     * Calcule la clé de contrôle d'un EAN-13 à partir de ses 12 premiers chiffres.
     * <p>
     * Chiffres en position impaire (1re, 3e...) pondérés par 1, positions paires par 3,
     * et la clé complète le total à la dizaine supérieure.
     * </p>
     */
    public String codeEan13(String base12) {
        if (base12 == null || !base12.matches("\\d{12}")) {
            throw new IllegalArgumentException(
                    "Un EAN-13 se calcule à partir de 12 chiffres, reçu : " + base12);
        }
        int somme = 0;
        for (int i = 0; i < base12.length(); i++) {
            int chiffre = base12.charAt(i) - '0';
            somme += (i % 2 == 0) ? chiffre : chiffre * 3;
        }
        int cle = (10 - somme % 10) % 10;
        return base12 + cle;
    }

    /**
     * Prépare une étiquette : la clé EAN-13 est calculée ici, une fois pour toutes.
     */
    public EtiquetteProduit etiquette(Produit produit) {
        return new EtiquetteProduit(produit.getReference(), produit.getDesignation(),
                produit.getPrixUnitaire(), produit.getUnite(),
                codeEan13(produit.getCodeEan13Base()));
    }

    /**
     * Prépare une carte de table dont le QR code ouvre le menu, avec la table en contexte.
     */
    public CarteTable carteTable(String nomTable) {
        return new CarteTable(nomTable, urlPublique + "/taverne/menu?table=" + segment(nomTable));
    }

    /**
     * Encode un segment d'URL en conservant les espaces sous la forme {@code %20}.
     */
    private static String segment(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
