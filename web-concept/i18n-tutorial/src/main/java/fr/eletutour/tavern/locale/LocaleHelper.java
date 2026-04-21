package fr.eletutour.tavern.locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

/**
 * Utilitaire de localisation (l10n) pour la taverne.
 * <p>
 * Centralise la résolution de la {@link Locale} depuis les en-têtes HTTP,
 * ainsi que le formatage des données sensibles à la culture :
 * monnaies, dates, et pluriels.
 * </p>
 *
 * <p>
 * <b>Pourquoi ici et pas dans le service ?</b> Les règles de formatage
 * sont indépendantes de la logique métier. Les séparer facilite les tests
 * et évite de polluer le service avec des préoccupations d'affichage.
 * </p>
 */
@ApplicationScoped
public class LocaleHelper {

    /**
     * Résout la {@link Locale} depuis l'en-tête {@code Accept-Language}.
     * <p>
     * Respecte l'ordre de préférence déclaré par le client (qualité q=...).
     * Retourne {@link Locale#FRENCH} comme locale par défaut de la taverne
     * si aucune langue acceptable n'est trouvée.
     * </p>
     *
     * @param headers les en-têtes HTTP de la requête courante
     * @return la locale résolue
     */
    public Locale resolveLocale(HttpHeaders headers) {
        List<Locale> acceptedLocales = headers.getAcceptableLanguages();

        if (acceptedLocales == null || acceptedLocales.isEmpty()) {
            return Locale.FRENCH;
        }

        // Le premier élément est déjà trié par préférence (q value) par JAX-RS
        Locale requested = acceptedLocales.get(0);

        // Wildcard "*" → locale par défaut
        if ("*".equals(requested.getLanguage())) {
            return Locale.FRENCH;
        }

        return requested;
    }

    // ---------------------------------------------------------------
    // Formatage monétaire (l10n)
    // ---------------------------------------------------------------

    /**
     * Formate un montant selon la convention monétaire de la locale.
     *
     * <ul>
     *   <li>{@code fr-FR} → "9,99 €"</li>
     *   <li>{@code en-US} → "$9.99"</li>
     *   <li>{@code de-DE} → "9,99 €"</li>
     *   <li>{@code es-ES} → "9,99 €"</li>
     * </ul>
     *
     * @param montant la valeur numérique
     * @param locale  la locale du client
     * @return la chaîne formatée avec symbole monétaire
     */
    public String formatMonnaie(double montant, Locale locale) {
        return NumberFormat.getCurrencyInstance(locale).format(montant);
    }

    // ---------------------------------------------------------------
    // Formatage de dates (l10n)
    // ---------------------------------------------------------------

    /**
     * Formate une date selon le style long de la locale.
     *
     * <ul>
     *   <li>{@code fr-FR} → "21 avril 2026"</li>
     *   <li>{@code en-US} → "April 21, 2026"</li>
     *   <li>{@code de-DE} → "21. April 2026"</li>
     *   <li>{@code es-ES} → "21 de abril de 2026"</li>
     * </ul>
     *
     * @param date   la date à formater
     * @param locale la locale du client
     * @return la chaîne de date localisée
     */
    public String formatDate(LocalDate date, Locale locale) {
        return DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(locale)
                .format(date);
    }

    // ---------------------------------------------------------------
    // Gestion des pluriels (l10n)
    // ---------------------------------------------------------------

    /**
     * Retourne la forme correcte (singulier ou pluriel) du mot "aventurier"
     * selon la locale et le nombre donné.
     *
     * <p>
     * Java ne dispose pas d'un moteur de pluriel universel natif.
     * Pour une application multilingue de production, on utiliserait
     * ICU4J ({@code com.ibm.icu.text.PluralRules}). Ici, les règles
     * sont implémentées manuellement pour les quatre langues supportées,
     * ce qui illustre bien la problématique l10n des pluriels.
     * </p>
     *
     * @param nombre le nombre d'aventuriers
     * @param locale la locale du client
     * @return "aventurier" ou "aventuriers" (ou équivalent dans la langue)
     */
    public String plurielAventurier(int nombre, Locale locale) {
        return switch (locale.getLanguage()) {
            case "en" -> nombre == 1 ? "adventurer" : "adventurers";
            case "de" -> "Abenteurer"; // invariable en allemand
            case "es" -> nombre == 1 ? "aventurero" : "aventureros";
            // Latin : nominatif 2ème déclinaison
            // singulier → "viator" (voyageur/aventurier), pluriel → "viatores"
            case "la" -> nombre == 1 ? "viator" : "viatores";
            default   -> nombre == 1 ? "aventurier" : "aventuriers"; // fr par défaut
        };
    }

    // ---------------------------------------------------------------
    // Cas particulier latin — monnaie (l10n sans standard ISO)
    // ---------------------------------------------------------------

    /**
     * Formate un montant en tenant compte du cas particulier du latin.
     * <p>
     * Le latin n'a pas de devise ISO moderne. On utilise le "denarius" (dn.)
     * comme convention DnD — valeur avec virgule décimale puis symbole.
     * Pour toutes les autres locales, on délègue à {@link #formatMonnaie}.
     * </p>
     *
     * <ul>
     *   <li>{@code la-LA} → "3,50 dn."</li>
     *   <li>{@code fr-FR} → "3,50 €"</li>
     *   <li>{@code en-US} → "$3.50"</li>
     * </ul>
     *
     * @param montant la valeur numérique
     * @param locale  la locale du client
     * @return la chaîne formatée
     */
    public String formatMonnaieAvecLatin(double montant, Locale locale) {
        if ("la".equals(locale.getLanguage())) {
            String montantFormate = String.format(Locale.FRENCH, "%.2f", montant);
            return montantFormate + " dn.";
        }
        return formatMonnaie(montant, locale);
    }
}
