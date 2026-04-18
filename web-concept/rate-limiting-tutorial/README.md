# Rate Limiting Tutorial - La Limite des Tournées

Gérer l'affluence du comptoir dans une Taverne Médiévale DnD, c'est indispensable pour ne pas tomber en rupture de stock en quelques secondes, ni tuer le tavernier d'épuisement. 

Ce module explique comment intégrer un **Rate Limiting** avec Quarkus en utilisant l'extension `quarkus-smallrye-fault-tolerance`.

## Cas d'utilisation
L'API de commande `POST /taverne/commande` a été annotée avec `@RateLimit` pour que chaque aventurier (client HTTP) soit limité à **3 verres toutes les 10 secondes**.

Si l'aventurier essaie de forcer un peu trop sur la commande, le tavernier le gronde et l'API renvoie **HTTP 429 Too Many Requests**.

## Comment tester ?

1. Démarrer le serveur HTTP de la taverne (assure-toi d'être dans le bon dossier) :
```bash
cd web-concept/rate-limiting-tutorial
../../mvnw quarkus:dev
```

2. Exécuter des appels HTTP (par exemple avec `curl`) :
```bash
# Lancer 4 appels rapides d'affilée :
for i in {1..4}; do curl -X POST http://localhost:8080/taverne/commande; echo ""; done
```

Tu observeras que la 4ème commande te retourne un joli message d'erreur !

## Configuration

### @RateLimit
La contrainte est gérée de cette manière :
```java
@RateLimit(value = 3, window = 10, windowUnit = ChronoUnit.SECONDS)
```
Un `RateLimitExceptionMapper` a été créé pour rattraper l'erreur interne et la convertir en réponse custom (code 429).

### @Retry (Le second souffle du Tavernier)
Sur la route `GET /taverne/cave`, le tavernier trebuche aléatoirement (ou de manière déterministe) dans les escaliers. L'annotation `@Retry(maxRetries = 3, delay = 200)` permet à Quarkus de relancer *automatiquement* la méthode jusqu'à 3 fois en espaçant les tentatives de 200ms.

### @Fallback (Le menu de secours)
Sur la route `GET /taverne/plat-du-jour`, s'il n'y a plus de ragoût de sanglier (Exception levée), l'annotation `@Fallback(fallbackMethod = "serveLeftovers")` permet de ne pas retourner une erreur 500 fatale à l'aventurier, mais de lui servir automatiquement autre chose avec un code 200 OK.
