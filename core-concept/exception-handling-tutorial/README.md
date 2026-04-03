# Gestion des exceptions dans Quarkus

Ce module démontre une approche industrialisée pour la gestion des erreurs dans une application Quarkus, en s'appuyant sur les standards modernes (RFC 7807) et la centralisation des comportements.

## Concept Clé : Industrialisation

Plutôt que de multiplier les `ExceptionMapper` disparates, nous utilisons une structure hiérarchique :

1.  **Modèle `Problem`** : Un format de réponse JSON unique et standardisé pour toutes les erreurs.
2.  **`AbstractExceptionMapper`** : Une classe de base qui centralise la construction de la réponse et permet l'enrichissement spécifique.
3.  **Exceptions Métier** : Une hiérarchie d'exceptions (`BusinessException`) qui portent leur propre code statut et titre.

## Architecture du projet

- `fr.eletutour.exception.model` : Contient le modèle `Problem`.
- `fr.eletutour.exception.business` : Contient les exceptions métier (`TavernNotFound`, `TavernCapacityReached`, etc.).
- `fr.eletutour.exception.mapper` : Contient les implémentations de `ExceptionMapper`.
- `fr.eletutour.resource` / `service` / `repository` : Une architecture classique pour illustrer la remontée d'exceptions.

## Cas d'usages démontrés

- **404 Not Found** : Ressource métier inexistante.
- **409 Conflict** : Violation de contrainte métier (doublon).
- **422 Unprocessable Entity** : Règle métier complexe non respectée avec enrichissement de données (ex: capacité d'accueil).
- **400 Bad Request** : Validation automatique des DTOs avec `Hibernate Validator`.
- **500 Internal Server Error** : Gestion sécurisée des erreurs imprévues via un mapper global.

## Comment tester ?

Le projet inclut des tests d'intégration complets dans `TavernResourceTest` qui valident chaque scénario et le format des réponses produites.
