package fr.eletutour.tavern.archives.dto;

import fr.eletutour.tavern.archives.model.Rang;

public record NouvelleProuesse(
        String titre,
        Rang rang,
        int annee,
        int recompense) {
}
