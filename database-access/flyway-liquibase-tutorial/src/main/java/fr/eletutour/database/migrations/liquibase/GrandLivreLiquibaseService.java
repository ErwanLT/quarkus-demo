package fr.eletutour.database.migrations.liquibase;

import fr.eletutour.database.migrations.comptes.Ecriture;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class GrandLivreLiquibaseService {

    @Transactional
    public List<Ecriture> facturer(String aventurier, int montantPiecesOr) {
        LigneDeCompteLiquibase consommation = new LigneDeCompteLiquibase(aventurier, "Tournee de biere",
            montantPiecesOr);
        consommation.persist();
        LigneDeCompteLiquibase taxe = new LigneDeCompteLiquibase(aventurier, "Taxe de la guilde",
            Math.max(1, montantPiecesOr / 10));
        taxe.persist();

        return List.of(consommation.versEcriture(), taxe.versEcriture());
    }

    public List<Ecriture> ecritures() {
        return LigneDeCompteLiquibase.<LigneDeCompteLiquibase>listAll().stream()
            .map(LigneDeCompteLiquibase::versEcriture)
            .toList();
    }

    @Transactional
    public long purger() {
        return LigneDeCompteLiquibase.deleteAll();
    }
}
