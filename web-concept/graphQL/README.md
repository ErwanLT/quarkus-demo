# Quarkus GraphQL Tutorials - La Taverne Enchantée

Ce projet est une série de tutoriels démontrant l'utilisation de **GraphQL** avec Quarkus, du côté serveur comme du côté client.

## Modules

Le projet est divisé en quatre modules principaux, chacun illustrant un aspect différent de l'intégration GraphQL.

### 1. [GraphQL Tutorial (Serveur)](graphql-tutorial)
Exposition de données via **MicroProfile GraphQL**.
- Mise en œuvre d'un schéma GraphQL pour une taverne médiévale.
- Utilisation de DTOs (Records Java 21).
- Gestion des relations (Aventuriers et Quêtes).
- Contraintes de schéma avec `@NonNull`.

### 2. [GraphQL Client Tutorial (Client TypeSafe)](graphql-client-tutorial)
Consommation de l'API via le **SmallRye GraphQL TypeSafe Client**.
- Utilisation d'une interface annotée avec `@GraphQLClientApi`.
- Approche orientée objet similaire à MicroProfile Rest Client.
- Mapping automatique des réponses vers des POJOs/Records.

### 3. [GraphQL Client Advanced Tutorial (Dynamic & Generator)](graphql-client-advanced-tutorial)
Consommation avancée de l'API via le **Dynamic Client** et le **Generator**.
- **Dynamic Client** : Construction programmatique de requêtes pour plus de flexibilité.
- **GraphQL Client Generator** : Utilisation de la nouvelle fonctionnalité SmallRye pour générer du code à partir du schéma (`.graphql`) et de fichiers de requêtes.

### 4. [GraphQL Fédération](federation-tutorial)
Découpage du domaine en **subgraphs** Quarkus (`aventurier-service`, `quete-service`), composés et routés par **Apollo Router**.
- Entité `Aventurier` possédée par un service et étendue par l'autre (`@Key`, `@Extends`, `@External`).
- Résolveur batché `@Source List<Aventurier>` et activation de `batch-resolving-enabled`.
- Supergraph généré avec `rover` et servi par le router sur le port `4000`.
- Détails de lancement : [README du module](federation-tutorial/README.md).

## Prérequis

- Java 21+
- Maven 3.9+
- Le serveur (module `graphql-tutorial`) doit être lancé pour que les clients puissent fonctionner.

## Lancement rapide

Pour lancer le serveur :
```bash
./mvnw quarkus:dev -pl web-concept/graphQL/graphql-tutorial
```

Pour lancer l'un des clients (dans un autre terminal) :
```bash
./mvnw quarkus:dev -pl web-concept/graphQL/graphql-client-tutorial
```