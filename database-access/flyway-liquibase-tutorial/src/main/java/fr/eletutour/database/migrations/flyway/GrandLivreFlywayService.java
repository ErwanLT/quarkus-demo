package fr.eletutour.database.migrations.flyway;

import fr.eletutour.database.migrations.comptes.Ecriture;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class GrandLivreFlywayService {

    @Transactional
    public List<Ecriture> facturer(String aventurier, int montantPiecesOr) {
        LigneDeCompteFlyway consommation = new LigneDeCompteFlyway(aventurier, "Tournee de biere", montantPiecesOr);
        consommation.persist();
        LigneDeCompteFlyway taxe = new LigneDeCompteFlyway(aventurier, "Taxe de la guilde",
            Math.max(1, montantPiecesOr / 10));
        taxe.persist();

        return List.of(consommation.versEcriture(), taxe.versEcriture());
    }

    public List<Ecriture> ecritures() {
        return LigneDeCompteFlyway.<LigneDeCompteFlyway>listAll().stream()
            .map(LigneDeCompteFlyway::versEcriture)
            .toList();
    }

    @Transactional
    public long purger() {
        return LigneDeCompteFlyway.deleteAll();
    }
}
