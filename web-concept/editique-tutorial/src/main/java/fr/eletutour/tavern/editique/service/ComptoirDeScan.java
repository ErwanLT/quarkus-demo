package fr.eletutour.tavern.editique.service;

import fr.eletutour.tavern.editique.repository.ArchivesTaverne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Le comptoir de scan : retrouve ce que désigne un code-barres lu.
 * <p>
 * Un QR code porte une URL complète, le téléphone l'ouvre tout seul. Un Code 128 ou un
 * EAN-13 ne porte qu'une valeur brute ({@code CMD-0042}, {@code 2000000000015}) : c'est
 * au comptoir de savoir vers quelle page elle mène.
 * </p>
 */
@ApplicationScoped
public class ComptoirDeScan {

    private static final Pattern NUMERO_COMMANDE = Pattern.compile("CMD-\\d+");
    private static final Pattern NUMERO_FACTURE = Pattern.compile("FAC-[A-Z0-9-]+");
    private static final Pattern EAN_13 = Pattern.compile("\\d{13}");

    /**
     * Ce que le comptoir a compris d'un code scanné.
     */
    public sealed interface Resolution {

        /** Le code désigne un élément connu : chemin de la page à ouvrir. */
        record Trouve(String chemin) implements Resolution {
        }

        /** Le code n'a pas pu être résolu, avec la raison à afficher. */
        record Inconnu(String code, String raison) implements Resolution {
        }
    }

    private final ArchivesTaverne archives;
    private final IdentificationService identification;

    @Inject
    public ComptoirDeScan(ArchivesTaverne archives, IdentificationService identification) {
        this.archives = archives;
        this.identification = identification;
    }

    /**
     * Résout un code lu par une douchette, une appli mobile ou saisi à la main.
     */
    public Resolution resoudre(String brut) {
        // Les douchettes ajoutent parfois des espaces ; les saisies manuelles, des minuscules
        String code = brut == null ? "" : brut.strip().toUpperCase(Locale.ROOT);
        if (code.isEmpty()) {
            return new Resolution.Inconnu(code, "Aucun code reçu.");
        }
        if (NUMERO_COMMANDE.matcher(code).matches()) {
            return archives.trouverCommande(code)
                    .<Resolution>map(c -> new Resolution.Trouve("/taverne/commandes/" + segment(code) + "/suivi"))
                    .orElseGet(() -> new Resolution.Inconnu(code, "Aucune commande ne porte ce numéro."));
        }
        if (NUMERO_FACTURE.matcher(code).matches()) {
            return archives.trouverFacture(code)
                    .<Resolution>map(f -> new Resolution.Trouve("/taverne/factures/" + segment(code) + "/suivi"))
                    .orElseGet(() -> new Resolution.Inconnu(code, "Aucune facture ne porte ce numéro."));
        }
        if (EAN_13.matcher(code).matches()) {
            return resoudreEan13(code);
        }
        return new Resolution.Inconnu(code, "Ce code ne ressemble ni à une commande, ni à une facture, ni à un EAN-13.");
    }

    private Resolution resoudreEan13(String code) {
        String base = code.substring(0, 12);
        // La clé de contrôle sert exactement à ça : refuser un code mal lu plutôt que d'ouvrir le mauvais produit
        if (!identification.codeEan13(base).equals(code)) {
            return new Resolution.Inconnu(code,
                    "Clé de contrôle invalide : le code a sans doute été mal lu ou mal saisi.");
        }
        return archives.trouverProduitParBaseEan(base)
                .<Resolution>map(p -> new Resolution.Trouve("/taverne/produits/" + segment(p.getReference())))
                .orElseGet(() -> new Resolution.Inconnu(code, "Aucun produit de la taverne ne porte ce code."));
    }

    private static String segment(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
