package fr.eletutour.database.migrations.registre;

import java.util.List;

public record EtatRegistre(String outil, String versionCourante, List<Travail> travauxAppliques,
                           List<Travail> travauxEnAttente) {
}
