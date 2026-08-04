package fr.eletutour.tavern.directive;

import io.smallrye.graphql.api.Directive;
import io.smallrye.graphql.api.DirectiveLocation;
import org.eclipse.microprofile.graphql.Description;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Signale un champ sensible dans le schéma GraphQL.
 * Purement déclaratif : n'entraîne aucun comportement côté serveur,
 * c'est au resolver concerné d'appliquer les règles de visibilité nécessaires.
 */
@Directive(on = DirectiveLocation.FIELD_DEFINITION)
@Description("Signale un champ sensible, à traiter avec prudence côté client.")
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensible {
}