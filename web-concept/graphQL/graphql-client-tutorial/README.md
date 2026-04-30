# GraphQL Client Tutorial - La Guilde des Aventuriers

Ce module démontre l'utilisation du **SmallRye GraphQL TypeSafe Client** pour consommer les services de la Taverne Enchantée.

## Concept

La **Guilde des Aventuriers** a besoin de récupérer des informations sur ses membres enregistrés à la taverne. Pour cela, elle utilise une approche "TypeSafe", où l'API GraphQL est modélisée par une interface Java. C'est l'approche la plus naturelle pour les développeurs Java, très proche de ce que propose MicroProfile Rest Client.

## Fonctionnement

### L'interface Client API
Le client est défini par une interface annotée :

```java
@GraphQLClientApi(configKey = "taverne")
public interface TaverneClientApi {
    List<Aventurier> aventuriers();
    Aventurier aventurier(Long id);
    Aventurier ajouterAventurier(AventurierInput input);
}
```

### Configuration
La configuration se fait dans le fichier `application.properties` :
```properties
quarkus.smallrye-graphql-client.taverne.url=http://localhost:8080/graphql
```

### Utilisation
Le client peut ensuite être injecté directement dans n'importe quel bean CDI :
```java
@Inject
TaverneClientApi taverneClient;
```

## Lancer le module

1. Assurez-vous que le serveur (`graphql-tutorial`) est lancé sur le port 8080.
2. Lancez ce client :
```bash
./mvnw quarkus:dev -pl graphql-client-tutorial
```
Le client est configuré pour tourner sur le port **9000** par défaut pour éviter les conflits avec le serveur.

Accédez à `http://localhost:9000/guilde/aventuriers` pour tester l'appel.
