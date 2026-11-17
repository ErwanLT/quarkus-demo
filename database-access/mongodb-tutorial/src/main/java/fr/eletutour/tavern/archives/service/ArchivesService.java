package fr.eletutour.tavern.archives.service;

import fr.eletutour.tavern.archives.dto.CreationDossier;
import fr.eletutour.tavern.archives.dto.DossierDto;
import fr.eletutour.tavern.archives.dto.NouvelleProuesse;
import fr.eletutour.tavern.archives.dto.StatistiquesClasse;
import fr.eletutour.tavern.archives.exception.DossierIntrouvableException;
import fr.eletutour.tavern.archives.exception.IdentifiantInvalideException;
import fr.eletutour.tavern.archives.exception.NomDejaPrisException;
import fr.eletutour.tavern.archives.model.ClasseAventurier;
import fr.eletutour.tavern.archives.model.DossierAventurier;
import fr.eletutour.tavern.archives.model.Prouesse;
import fr.eletutour.tavern.archives.model.Rang;
import fr.eletutour.tavern.archives.repository.DossierAventurierRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class ArchivesService {

    @Inject
    DossierAventurierRepository repository;

    public List<DossierDto> lister(ClasseAventurier classe, Rang rang, int page, int taille) {
        return repository.chercher(classe, rang, page, taille).stream()
                .map(DossierDto::depuis)
                .toList();
    }

    public DossierDto trouver(String id) {
        return DossierDto.depuis(entite(id));
    }

    public DossierDto creer(CreationDossier creation) {
        if (repository.parNom(creation.nom()).isPresent()) {
            throw new NomDejaPrisException(
                    "Un dossier au nom de " + creation.nom() + " existe déjà dans les archives.");
        }

        DossierAventurier dossier = new DossierAventurier();
        dossier.nom = creation.nom();
        dossier.classe = creation.classe();
        dossier.niveau = creation.niveau();
        dossier.bourse = creation.bourse();
        repository.persist(dossier);
        return DossierDto.depuis(dossier);
    }

    public DossierDto ajouterProuesse(String id, NouvelleProuesse prouesse) {
        DossierAventurier dossier = entite(id);
        dossier.prouesses.add(new Prouesse(
                prouesse.titre(), prouesse.rang(), prouesse.annee(), prouesse.recompense()));
        repository.update(dossier);
        return DossierDto.depuis(dossier);
    }

    public List<StatistiquesClasse> statistiques() {
        return repository.repartition();
    }

    private DossierAventurier entite(String id) {
        return repository.findByIdOptional(identifiant(id))
                .orElseThrow(() -> new DossierIntrouvableException(
                        "Aucun dossier ne porte l'identifiant " + id + "."));
    }

    private ObjectId identifiant(String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException exception) {
            throw new IdentifiantInvalideException(
                    "L'identifiant " + id + " n'est pas un ObjectId MongoDB valide.");
        }
    }
}
