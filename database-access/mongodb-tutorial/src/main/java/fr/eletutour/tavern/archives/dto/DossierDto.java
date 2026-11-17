package fr.eletutour.tavern.archives.dto;

import fr.eletutour.tavern.archives.model.DossierAventurier;
import fr.eletutour.tavern.archives.model.Prouesse;

import java.util.List;

/**
 * Représentation HTTP d'un dossier. L'identifiant Mongo est un {@code ObjectId} :
 * on l'expose en hexadécimal plutôt que de laisser Jackson deviner sa structure.
 */
public record DossierDto(
        String id,
        String nom,
        String classe,
        int niveau,
        int bourse,
        List<ProuesseDto> prouesses) {

    public static DossierDto depuis(DossierAventurier dossier) {
        return new DossierDto(
                dossier.id == null ? null : dossier.id.toHexString(),
                dossier.nom,
                dossier.classe == null ? null : dossier.classe.name(),
                dossier.niveau,
                dossier.bourse,
                dossier.prouesses.stream().map(ProuesseDto::depuis).toList());
    }

    public record ProuesseDto(String titre, String rang, int annee, int recompense) {

        static ProuesseDto depuis(Prouesse prouesse) {
            return new ProuesseDto(
                    prouesse.titre,
                    prouesse.rang == null ? null : prouesse.rang.name(),
                    prouesse.annee,
                    prouesse.recompense);
        }
    }
}
