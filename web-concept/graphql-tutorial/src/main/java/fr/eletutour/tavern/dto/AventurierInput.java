package fr.eletutour.tavern.dto;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

/**
 * Données nécessaires pour ajouter un aventurier via l'API.
 */
@Name("AventurierInput")
@Description("Données pour la création d'un aventurier")
public record AventurierInput(@NonNull @Description("Nom de l'aventurier") String nom,
    @NonNull @Description("Classe choisie") String classe,
    @NonNull @Description("Niveau initial (doit être > 0)") Integer niveau) {}
