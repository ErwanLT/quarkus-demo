# GraphQL Fédération - La Taverne en plusieurs royaumes

[Article](https://www.sfeir.dev/back/la-taverne-graphql-un-royaume-de-tavernes/)

Découpage d'un domaine en **subgraphs Quarkus** fédérés, composés et routés par **Apollo Router**.
Chaque service ne connaît que sa partie du modèle : `aventurier-service` possède l'entité `Aventurier`,
`quete-service` l'étend en n'en connaissant que la clé `id`.

## Architecture

```text
                 ┌──────────────────────────────┐
                 │  Apollo Router (:4000)       │
                 │  supergraph.graphql généré    │
                 └───────┬──────────────┬───────┘
                         │              │
          ┌──────────────▼───┐   ┌──────▼─────────────┐
          │ aventurier-service│   │  quete-service     │
          │ Quarkus :8081     │   │  Quarkus :8082     │
          │ @Key Aventurier   │   │  @Extends Aventurier│
          └───────────────────┘   └────────────────────┘
```

- `aventurier-service` : `@GraphQLApi` + `@Key(fields = @FieldSet("id"))` sur `Aventurier`, avec un résolveur
  `aventurier(@Id Long id)` qui sert aussi de référence pour les autres subgraphs.
- `quete-service` : `@Extends` sur un `Aventurier` réduit à son `id` (`@External`), et un résolveur batché
  `quetes(@Source List<Aventurier>)` activé par `quarkus.smallrye-graphql.federation.batch-resolving-enabled=true`.
- `router/` : configuration Apollo Router, `docker-compose.yml`, et un script qui contourne deux incompatibilités
  connues entre SmallRye GraphQL et `rover` (voir les commentaires dans le script).

## Prérequis

| Outil | Rôle |
|---|---|
| JDK 21+ | Les deux subgraphs |
| Docker | Apollo Router (`ghcr.io/apollographql/router`) |
| [`rover`](https://www.apollographql.com/docs/rover/getting-started/) | Introspection des subgraphs et composition du supergraph (`npm install -g @apollo/rover`) |

## Lancer les subgraphs

Depuis la racine du dépôt, dans deux terminaux :

```bash
# Terminal 1
./mvnw -pl web-concept/graphQL/federation-tutorial/aventurier-service quarkus:dev

# Terminal 2
./mvnw -pl web-concept/graphQL/federation-tutorial/quete-service quarkus:dev
```

Les endpoints GraphQL sont accessibles sur :

| Service | GraphQL | Dev UI |
|---|---|---|
| `aventurier-service` | <http://localhost:8081/graphql> | <http://localhost:8081/q/dev-ui> |
| `quete-service` | <http://localhost:8082/graphql> | <http://localhost:8082/q/dev-ui> |

À ce stade, chaque subgraph répond seul : `aventurier-service` ne connaît pas les quêtes, et
`quete-service` ne sait rien des noms ou des classes d'aventuriers. C'est le router qui recompose les deux.

## Générer le supergraph et démarrer le router

`aventurier.graphql`, `quete.graphql` et `supergraph.graphql` sont générés localement et volontairement
absents du dépôt (`.gitignore`). Les subgraphs doivent tourner avant cette étape.

```bash
cd web-concept/graphQL/federation-tutorial/router

# 1. Introspecte les deux services et corrige les schémas pour rover
./fix-federation-schema.sh

# 2. Compose le supergraph consommé par le router
rover supergraph compose --config ./supergraph-config.yaml > supergraph.graphql

# 3. Démarre Apollo Router
docker compose up
```

Le router écoute sur <http://localhost:4000>. Apollo Sandbox est activé (`sandbox.enabled: true` dans
`router/router.yaml`) : ouvrir l'URL dans un navigateur pour requêter le supergraph.

```graphql
query {
  aventuriers {
    id
    nom
    classe
    niveau
    quetes {
      titre
      difficulte
      recompenseOr
    }
  }
}
```

`aventuriers` et les champs de l'entité viennent de `aventurier-service`, `quetes` de `quete-service` :
le router résout la référence dans le premier subgraph puis complète la réponse avec le second.

## Pourquoi le script `fix-federation-schema.sh` ?

Deux contournements, documentés en tête du script :

1. SmallRye nomme son scalaire de fédération `FieldSet` au lieu de `_FieldSet`, attendu par `rover`
   ([smallrye-graphql#2065](https://github.com/smallrye/smallrye-graphql/issues/2065)).
2. SmallRye n'émet pas de `extend schema @link(...)` ; sans ce lien, `rover` plafonne l'interprétation
   à la fédération v2.4 et ne connaît pas l'argument `label` de `@override`. Le script le retire,
   cet argument n'étant pas utilisé ici.

## Points d'attention

- Les deux services n'ont pas de tests automatisés ; la validation se fait via le router et Sandbox.
- `aventurier.graphql`, `quete.graphql` et `supergraph.graphql` sont des artefacts générés, ignorés par Git :
  il faut rejouer `fix-federation-schema.sh` et `rover supergraph compose` après toute modification du schéma.
