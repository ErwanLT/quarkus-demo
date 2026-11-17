package fr.eletutour.tavern.archives.dto;

import fr.eletutour.tavern.archives.model.ClasseAventurier;

public record CreationDossier(
        String nom,
        ClasseAventurier classe,
        int niveau,
        int bourse) {
}
