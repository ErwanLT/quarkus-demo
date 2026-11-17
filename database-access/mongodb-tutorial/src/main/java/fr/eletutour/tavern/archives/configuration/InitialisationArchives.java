package fr.eletutour.tavern.archives.configuration;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import fr.eletutour.tavern.archives.model.ClasseAventurier;
import fr.eletutour.tavern.archives.model.DossierAventurier;
import fr.eletutour.tavern.archives.model.Prouesse;
import fr.eletutour.tavern.archives.model.Rang;
import fr.eletutour.tavern.archives.repository.DossierAventurierRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Prépare les archives au démarrage : index unique puis jeu de démonstration.
 */
@ApplicationScoped
public class InitialisationArchives {

    private static final Logger LOG = Logger.getLogger(InitialisationArchives.class);

    @Inject
    DossierAventurierRepository repository;

    void onStart(@Observes StartupEvent event) {
        repository.mongoCollection().createIndex(
                Indexes.ascending("nom"),
                new IndexOptions().unique(true));

        if (repository.count() > 0) {
            LOG.infof("Archives déjà peuplées : %d dossiers.", repository.count());
            return;
        }

        repository.persist(dossiers());
        LOG.infof("Archives initialisées : %d dossiers.", repository.count());
    }

    private List<DossierAventurier> dossiers() {
        List<DossierAventurier> dossiers = new ArrayList<>();

        DossierAventurier brune = dossier("Brune la rôdeuse", ClasseAventurier.RODEUR, 7, 120);
        brune.prouesses.add(new Prouesse("Le troll des cavernes", Rang.TROLL, 1021, 50));
        dossiers.add(brune);

        DossierAventurier aldric = dossier("Aldric le barde", ClasseAventurier.BARDE, 5, 60);
        aldric.prouesses.add(new Prouesse("Les gobelins de la combe", Rang.GOBELIN, 1022, 10));
        dossiers.add(aldric);

        DossierAventurier mirelle = dossier("Mirelle l'ensorceleuse", ClasseAventurier.MAGE, 9, 300);
        mirelle.prouesses.add(new Prouesse("Le dragon de la combe", Rang.DRAGON, 1023, 200));
        dossiers.add(mirelle);

        DossierAventurier gorm = dossier("Gorm le guerrier", ClasseAventurier.GUERRIER, 6, 80);
        gorm.prouesses.add(new Prouesse("Le troll du pont", Rang.TROLL, 1020, 40));
        dossiers.add(gorm);

        return dossiers;
    }

    private DossierAventurier dossier(String nom, ClasseAventurier classe, int niveau, int bourse) {
        DossierAventurier dossier = new DossierAventurier();
        dossier.nom = nom;
        dossier.classe = classe;
        dossier.niveau = niveau;
        dossier.bourse = bourse;
        return dossier;
    }
}
