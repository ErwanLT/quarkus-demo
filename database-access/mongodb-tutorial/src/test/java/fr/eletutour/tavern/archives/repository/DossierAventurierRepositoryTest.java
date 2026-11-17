package fr.eletutour.tavern.archives.repository;

import com.mongodb.MongoWriteException;
import fr.eletutour.tavern.archives.dto.StatistiquesClasse;
import fr.eletutour.tavern.archives.model.ClasseAventurier;
import fr.eletutour.tavern.archives.model.DossierAventurier;
import fr.eletutour.tavern.archives.model.Rang;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class DossierAventurierRepositoryTest {

    @Inject
    DossierAventurierRepository repository;

    @Test
    @DisplayName("La requête à points traverse le tableau des prouesses")
    void chercheDansLeTableauImbrique() {
        List<DossierAventurier> trolls = repository.chercher(null, Rang.TROLL, 0, 10);

        assertTrue(trolls.stream().anyMatch(dossier -> dossier.nom.equals("Brune la rôdeuse")));
        assertTrue(trolls.stream().anyMatch(dossier -> dossier.nom.equals("Gorm le guerrier")));
    }

    @Test
    @DisplayName("La pagination de Panache renvoie une page de la taille demandée")
    void pagineLesDossiers() {
        assertEquals(2, repository.chercher(null, null, 0, 2).size());
        assertEquals(2, repository.chercher(null, null, 1, 2).size());
    }

    @Test
    @DisplayName("L'index unique sur le nom est bien créé au démarrage")
    void refuseUnNomEnDoubleAuNiveauMongo() {
        String nom = "Ombre de test " + UUID.randomUUID();
        DossierAventurier premier = dossier(nom);

        try {
            repository.persist(premier);
            assertThrows(MongoWriteException.class, () -> repository.persist(dossier(nom)));
        } finally {
            repository.delete(new Document("nom", nom));
        }
    }

    @Test
    @DisplayName("L'agrégation répartit les effectifs par classe")
    void agregeParClasse() {
        List<StatistiquesClasse> statistiques = repository.repartition();

        StatistiquesClasse mages = statistiques.stream()
                .filter(statistique -> statistique.classe().equals("MAGE"))
                .findFirst()
                .orElseThrow();

        assertTrue(mages.effectif() >= 1);
        assertTrue(mages.bourse() >= 300);
    }

    private DossierAventurier dossier(String nom) {
        DossierAventurier dossier = new DossierAventurier();
        dossier.nom = nom;
        dossier.classe = ClasseAventurier.RODEUR;
        dossier.niveau = 1;
        dossier.bourse = 0;
        return dossier;
    }
}
