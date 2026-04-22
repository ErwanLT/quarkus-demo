package fr.eletutour.tavern.service;

import fr.eletutour.tavern.locale.LocaleHelper;
import fr.eletutour.tavern.messages.TavernMessages;
import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.MessageBundles;
import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service métier de la taverne, avec i18n et l10n intégrés.
 * <p>
 * Chaque méthode représente un cas d'usage métier explicite et accepte une
 * {@link Locale} déjà résolue par la couche API. Les mécanismes de résilience
 * (Rate Limiting, Retry, Fallback) sont inchangés.
 * </p>
 */
@ApplicationScoped
public class TavernService {

    private static final Logger LOG = Logger.getLogger(TavernService.class);

    @Inject
    LocaleHelper localeHelper;

    private final AtomicInteger cellarTrips = new AtomicInteger(0);

    // ---------------------------------------------------------------
    // Comptoir — Rate Limiting
    // ---------------------------------------------------------------

    /**
     * Commande une bière, limitée à 3 appels toutes les 10 secondes.
     * Le message de confirmation est retourné dans la langue du client.
     *
     * @param locale locale métier résolue par la couche API
     * @return la confirmation du tavernier, localisée
     */
    @RateLimit(value = 3, window = 10, windowUnit = ChronoUnit.SECONDS)
    public String orderBeer(Locale locale) {
        LOG.infof("Commande de bière — locale : %s", locale);
        return resolveMessages(locale).biereFraiche();
    }

    // ---------------------------------------------------------------
    // Cave — Retry
    // ---------------------------------------------------------------

    /**
     * Remonte une bouteille de la cave.
     * <p>
     * Chaque tentative a 60% de chances d'échouer (comportement probabiliste,
     * adapté à la concurrence contrairement à la version déterministe de
     * l'article sur le fault tolerance). Le retry rejoue jusqu'à 3 fois
     * avec 200ms de délai entre chaque tentative.
     * </p>
     *
     * @param locale locale métier résolue par la couche API
     * @return le message de succès localisé après les éventuelles tentatives
     */
    @Retry(maxRetries = 3, delay = 200)
    public String fetchFromCellar(Locale locale) {
        TavernMessages localizedMessages = resolveMessages(locale);
        int attempt = cellarTrips.incrementAndGet();

        if (Math.random() < 0.60) {
            String warning = localizedMessages.caveTrebuche(attempt);
            LOG.warn(warning);
            throw new RuntimeException("Tavernier tombé dans les escaliers");
        }

        LOG.infof("Cave réussie — tentative %d", attempt);
        return localizedMessages.caveSucces();
    }

    /**
     * Réinitialise le compteur de tentatives de la cave.
     *
     * @param locale locale métier résolue par la couche API
     * @return le message de confirmation localisé
     */
    public String resetCellar(Locale locale) {
        cellarTrips.set(0);
        LOG.infof("Reset cave demandé — locale : %s", locale);
        return resolveMessages(locale).caveReset();
    }

    // ---------------------------------------------------------------
    // Cuisine — Fallback
    // ---------------------------------------------------------------

    /**
     * Commande le plat du jour. Si la marmite est vide, déclenche le fallback.
     *
     * @param empty   {@code true} pour simuler une rupture de stock
     * @param locale locale métier résolue par la couche API
     * @return le plat du jour ou le plat de secours, localisé
     */
    @Fallback(fallbackMethod = "serveLeftovers")
    public String orderPlatDuJour(boolean empty, Locale locale) {
        TavernMessages localizedMessages = resolveMessages(locale);
        LOG.infof("Commande plat du jour — locale : %s, marmite vide: %s", locale, empty);
        if (empty) {
            throw new RuntimeException("Plus aucun ragoût de sanglier !");
        }
        return localizedMessages.platDuJour();
    }

    /**
     * Méthode de fallback — même signature que {@link #orderPlatDuJour}.
     *
     * @param empty   ignoré (paramètre requis par le contrat Fallback)
     * @param locale locale métier résolue par la couche API
     * @return le plat de secours localisé
     */
    public String serveLeftovers(boolean empty, Locale locale) {
        LOG.infof("Fallback cuisine activé — locale : %s", locale);
        return resolveMessages(locale).platFallback();
    }

    // ---------------------------------------------------------------
    // Accueil & informations générales (l10n avancée)
    // ---------------------------------------------------------------

    /**
     * Accueille un aventurier par son nom, dans sa langue.
     *
     * @param nom     le nom ou titre de l'aventurier
     * @param locale locale métier résolue par la couche API
     * @return le message d'accueil localisé
     */
    public String accueillir(String nom, Locale locale) {
        LOG.infof("Accueil aventurier — locale : %s, nom : %s", locale, nom);
        return resolveMessages(locale).bienvenue(nom);
    }

    /**
     * Annonce le prix d'un article avec devise localisée (l10n monétaire).
     *
     * @param article  le nom de l'article
     * @param montant  le montant en valeur numérique
     * @param locale locale métier résolue par la couche API
     * @return la chaîne de prix formatée selon la locale du client
     */
    public String annoncerPrix(String article, double montant, Locale locale) {
        String prixFormate = localeHelper.formatMonnaieAvecLatin(montant, locale);
        LOG.infof("Annonce prix — locale : %s, article : %s, montant : %.2f", locale, article, montant);
        return resolveMessages(locale).prix(article, prixFormate);
    }

    /**
     * Annonce l'affluence courante avec accord du pluriel localisé (l10n pluriels).
     *
     * @param nombre  le nombre d'aventuriers présents
     * @param locale locale métier résolue par la couche API
     * @return la phrase d'affluence localisée avec pluriel correct
     */
    public String annoncerAffluence(int nombre, Locale locale) {
        String libelle = localeHelper.plurielAventurier(nombre, locale);
        LOG.infof("Annonce affluence — locale : %s, nombre : %d", locale, nombre);
        return resolveMessages(locale).affluence(nombre, libelle);
    }

    private TavernMessages resolveMessages(Locale locale) {
        try {
            return MessageBundles.get(TavernMessages.class, Localized.Literal.of(locale.toLanguageTag()));
        } catch (IllegalStateException e) {
            try {
                return MessageBundles.get(TavernMessages.class, Localized.Literal.of(locale.getLanguage()));
            } catch (IllegalStateException ignored) {
                return MessageBundles.get(TavernMessages.class);
            }
        }
    }
}
