# GraphQL Tutorial - La Taverne Enchantée

Ce module démontre l'utilisation de **MicroProfile GraphQL** avec Quarkus pour exposer les données des aventuriers de la taverne.

## Concept

Dans cet univers, la taverne ne se contente plus de servir des bières ; elle gère un registre complexe d'aventuriers, de leurs classes et des quêtes qu'ils ont accomplies. Contrairement à une API REST classique, GraphQL permet aux clients (comme une guilde d'aventuriers ou un barde itinérant) de demander exactement les informations dont ils ont besoin.

## Architecture

Le module suit les principes de conception industrialisés :
- **Controller (TaverneResource)** : Point d'entrée GraphQL "plat", utilisant des **DTOs (Java 21 Records)** pour l'isolation et la documentation (OpenAPI/GraphQL).
- **Service (TaverneService)** : Couche métier contenant la logique de validation et la gestion des données.
- **Modèle** : Objets du domaine interne.
- **Contrat Fort** : Utilisation de `@NonNull` pour garantir la non-nullité des champs dans le schéma généré.

## Exemples de requêtes

### Lister les aventuriers
```graphql
query {
  aventuriers {
    nom
    classe
    niveau
    quetes {
      titre
      recompenseOr
    }
  }
}
```

### Ajouter un aventurier
```graphql
mutation {
  ajouterAventurier(input: {
    nom: "Gimli",
    classe: "Guerrier Nain",
    niveau: 15
  }) {
    id
    nom
  }
}
```

## Lancer le module

```bash
./mvnw quarkus:dev -pl web-concept/graphql-tutorial
```
L'interface **GraphQL UI** sera accessible sur `http://localhost:8080/q/graphql-ui`.
