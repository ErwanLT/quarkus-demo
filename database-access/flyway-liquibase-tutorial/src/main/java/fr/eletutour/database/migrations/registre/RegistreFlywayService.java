package fr.eletutour.database.migrations.registre;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class RegistreFlywayService {

    @Inject
    Flyway flyway;

    public EtatRegistre etat() {
        MigrationInfoService informations = flyway.info();
        MigrationInfo courante = informations.current();
        String versionCourante = courante == null || courante.getVersion() == null
            ? null
            : courante.getVersion().toString();

        return new EtatRegistre(
            "flyway",
            versionCourante,
            versTravaux(informations.applied()),
            versTravaux(informations.pending())
        );
    }

    private List<Travail> versTravaux(MigrationInfo[] migrations) {
        return Arrays.stream(migrations)
            .map(this::versTravail)
            .toList();
    }

    private Travail versTravail(MigrationInfo migration) {
        Date installeLe = migration.getInstalledOn();
        return new Travail(
            migration.getVersion() == null ? null : migration.getVersion().toString(),
            migration.getDescription(),
            migration.getInstalledBy(),
            installeLe == null ? null : installeLe.toInstant(),
            migration.getState().name()
        );
    }
}
