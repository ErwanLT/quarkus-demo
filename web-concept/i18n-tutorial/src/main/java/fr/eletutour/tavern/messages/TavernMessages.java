package fr.eletutour.tavern.messages;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

/**
 * Contrat typé des messages de la taverne.
 * <p>
 * Chaque méthode correspond à une clé traduite dans les fichiers
 * TavernMessages_fr.properties, TavernMessages_en.properties, etc.
 * Quarkus génère l'implémentation à la compilation et résout automatiquement
 * la locale depuis l'en-tête HTTP {@code Accept-Language}.
 * </p>
 *
 * <p><b>i18n</b> : structure du code indépendante de toute langue.</p>
 * <p><b>l10n</b> : traductions concrètes dans les fichiers .properties.</p>
 */
@MessageBundle("TavernMessages")
public interface TavernMessages {

    // ---------------------------------------------------------------
    // Comptoir — commande de bière
    // ---------------------------------------------------------------

    /** Confirmation de service d'une bière. */
    @Message("Tiens, une bonne pinte bien fraîche !")
    String biereFraiche();

    /** Rejet par le tavernier lorsque la limite de débit est atteinte. */
    @Message("Holà l'ami ! Laisse-moi le temps de tirer ta bière, le tonneau n'est pas infini !")
    String biereRateLimit();

    // ---------------------------------------------------------------
    // Cave — remontée de bouteille avec retry
    // ---------------------------------------------------------------

    /** Message de succès quand le tavernier remonte enfin la bouteille. */
    @Message("Voilà votre prestigieux Vin Elfique millésimé !")
    String caveSucces();

    /**
     * Message d'échec lors d'une tentative de descente à la cave.
     *
     * @param tentative numéro de la tentative en cours
     */
    @Message("Oups ! Le tavernier a glissé sur une marche vers la cave ! (Tentative {tentative})")
    String caveTrebuche(int tentative);

    /** Confirmation de réinitialisation du compteur de la cave. */
    @Message("Cave réinitialisée, le tavernier reprend ses esprits.")
    String caveReset();

    // ---------------------------------------------------------------
    // Cuisine — plat du jour avec fallback
    // ---------------------------------------------------------------

    /** Plat du jour disponible. */
    @Message("Voici un délicieux ragoût de sanglier !")
    String platDuJour();

    /** Fallback quand la marmite est vide. */
    @Message("Désolé l'ami, la marmite est vide... Tiens, un morceau de pain dur et du fromage sec en lot de consolation.")
    String platFallback();

    // ---------------------------------------------------------------
    // Accueil & informations générales
    // ---------------------------------------------------------------

    /**
     * Message d'accueil personnalisé à l'entrée de la taverne.
     *
     * @param nom le prénom ou titre de l'aventurier
     */
    @Message("Bienvenue dans la Taverne du Dragon Boiteux, {nom} !")
    String bienvenue(String nom);

    /**
     * Annonce du prix d'un article avec devise localisée.
     *
     * @param article  le nom de l'article commandé
     * @param prix     le prix formaté (devise et format selon la locale)
     */
    @Message("Le {article} vous coûtera {prix}.")
    String prix(String article, String prix);

    /**
     * Compte des aventuriers présents, avec accord du pluriel géré côté appelant.
     *
     * @param compte   le nombre d'aventuriers
     * @param libelle  "aventurier" ou "aventuriers" selon le compte
     */
    @Message("Il y a actuellement {compte} {libelle} dans la salle.")
    String affluence(int compte, String libelle);
}
