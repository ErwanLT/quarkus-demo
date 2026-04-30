# GraphQL Client Advanced Tutorial - Dynamic & Generator

Ce module explore des méthodes plus avancées pour consommer une API GraphQL avec Quarkus : le **Dynamic Client** et le **GraphQL Client Generator**.

## 1. Dynamic Client

Le client dynamique permet de construire des requêtes programmatiquement. C'est utile lorsque la structure de la requête n'est pas connue à la compilation ou que vous souhaitez un contrôle total sur les champs demandés sans créer de POJOs complexes pour chaque variation.

### Exemple d'utilisation
```java
@Inject
@GraphQLClient("tavern-dynamic")
DynamicGraphQLClient dynamicClient;

public List<Map> getAllAventurier() throws ExecutionException, InterruptedException {
    Document query = document(
            operation(
                    field("aventuriers",
                            field("nom"),
                            field("niveau"),
                            field("quetes",
                                    field("titre")
                            )
                    )
            )
    );
    Response response = dynamicClient.executeSync(query);
    return response.getList(Map.class, "aventuriers");
}
```

## 2. GraphQL Client Generator

C'est une fonctionnalité plus récente (SmallRye) qui génère le code client (modèles et méthodes d'appel) à partir du schéma GraphQL et de définitions de requêtes.

### Fonctionnement
On définit une interface avec le schéma et les requêtes souhaitées :
```java
@GraphQLSchema("resource:schema.graphql")
@GraphQLQuery("""
query Aventuriers {
  aventuriers {
    id
    nom
    classe
  }
}
""")
public interface TavernQuery {
}
```

Le générateur s'occupe de créer les classes nécessaires lors de la compilation via un processeur d'annotations.

## Lancer le module

1. Assurez-vous que le serveur (`graphql-tutorial`) est lancé sur le port 8080.
2. Lancez ce client :
```bash
./mvnw quarkus:dev -pl graphql-client-advanced-tutorial
```
Le client tourne sur le port **9000** par défaut.

Testez l'appel dynamique : `http://localhost:9000/tavern/dynamic`
