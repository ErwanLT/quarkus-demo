package fr.eletutour.tavern.archives.dto;

import java.util.List;

/** Résultats d'une page avec les informations nécessaires à la navigation. */
public record PageDossiers(List<DossierDto> elements, int page, int taille, long total, long nombreDePages) {
}
