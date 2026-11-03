package fr.eletutour.database.migrations.registre;

import io.quarkus.liquibase.LiquibaseDataSource;
import io.quarkus.liquibase.LiquibaseFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeSetStatus;
import liquibase.exception.LiquibaseException;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class RegistreLiquibaseService {

    @Inject
    @LiquibaseDataSource("liquibase")
    LiquibaseFactory liquibaseFactory;

    public EtatRegistre etat() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            List<ChangeSetStatus> statuts = liquibase.getChangeSetStatuses(new Contexts());
            List<Travail> appliques = statuts.stream()
                .filter(ChangeSetStatus::getPreviouslyRan)
                .map(this::versTravail)
                .toList();
            List<Travail> enAttente = statuts.stream()
                .filter(statut -> !statut.getPreviouslyRan())
                .map(this::versTravail)
                .toList();

            String versionCourante = appliques.isEmpty() ? null : appliques.get(appliques.size() - 1).version();
            return new EtatRegistre("liquibase", versionCourante, appliques, enAttente);
        } catch (LiquibaseException liquibaseException) {
            throw new IllegalStateException("Lecture du registre Liquibase impossible", liquibaseException);
        }
    }

    private Travail versTravail(ChangeSetStatus statut) {
        ChangeSet changeSet = statut.getChangeSet();
        Date executeLe = statut.getDateLastExecuted();
        return new Travail(
            changeSet.getId(),
            changeSet.getDescription(),
            changeSet.getAuthor(),
            executeLe == null ? null : executeLe.toInstant(),
            statut.getPreviouslyRan() ? "SUCCESS" : "PENDING"
        );
    }
}
