package fr.eletutour.tavern.archives.exception.business;

import java.util.Map;

/**
 * Seule exception métier du module : les fabriques évitent de disperser les
 * statuts et les messages dans le service, et transportent le contexte utile
 * au client (identifiant, nom, code MongoDB).
 */
public class ArchivesException extends BusinessException {

    private final ArchivesError error;
    private final Map<String, Object> contexte;

    private ArchivesException(ArchivesError error, Map<String, Object> contexte, Object... detailArgs) {
        super(error.getStatus(), error.getTitle(), error.formatDetail(detailArgs));
        this.error = error;
        this.contexte = Map.copyOf(contexte);
    }

    public static ArchivesException dossierIntrouvable(String identifiant) {
        return new ArchivesException(ArchivesError.DOSSIER_INTROUVABLE,
                Map.of("identifiant", identifiant), identifiant);
    }

    public static ArchivesException identifiantInvalide(String identifiant) {
        return new ArchivesException(ArchivesError.IDENTIFIANT_INVALIDE,
                Map.of("identifiant", identifiant), identifiant);
    }

    public static ArchivesException nomDejaPris(String nom) {
        return new ArchivesException(ArchivesError.NOM_DEJA_PRIS, Map.of("nom", nom));
    }

    /** Cas de la course entre deux requêtes : le nom n'est pas connu du mapper. */
    public static ArchivesException nomDejaPris() {
        return new ArchivesException(ArchivesError.NOM_DEJA_PRIS, Map.of());
    }

    public static ArchivesException ecritureImpossible(int codeMongo) {
        return new ArchivesException(ArchivesError.ECRITURE_IMPOSSIBLE,
                Map.of("codeMongo", codeMongo));
    }

    public ArchivesError getError() {
        return error;
    }

    public Map<String, Object> getContexte() {
        return contexte;
    }
}
