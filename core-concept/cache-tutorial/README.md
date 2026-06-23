# Cache - L'ardoise magique 🪄

> *"Plutôt que de redescendre à la cave ouvrir le grand livre à chaque fois qu'un aventurier
> demande le prix d'un plat, le tavernier lève simplement les yeux vers son ardoise magique."*

Ce module démontre l'utilisation de **`quarkus-cache`** (backed by Caffeine) pour mettre en
cache les résultats coûteux d'une méthode de service, en utilisant la métaphore d'une taverne
médiévale.

## Concept : L'ardoise magique

| Élément de la métaphore | Équivalent technique |
|---|---|
| L'ardoise au-dessus du comptoir | Le cache Caffeine en mémoire (`ardoise-menu`) |
| Le tavernier qui lève les yeux | `@CacheResult` : retourne le résultat mis en cache |
| Descendre à la cave | Appel coûteux à `CaveRepository` (200 ms simulés) |
| Inscrire sur l'ardoise | Stockage automatique du résultat après un cache miss |
| Effacer un jour de l'ardoise | `@CacheInvalidate` : invalide une entrée par clé |
| Effacer toute l'ardoise | `@CacheInvalidateAll` : vide tout le cache |

## Annotations utilisées

### `@CacheResult(cacheName = "ardoise-menu")`

La plus importante. Placée sur `MenuService.obtenirMenuDuJour(String jour)` :

- **Cache hit** : le résultat est sur l'ardoise → retourné **immédiatement**, la méthode ne s'exécute pas.
- **Cache miss** : rien sur l'ardoise → la méthode s'exécute, et le résultat est **inscrit sur l'ardoise** pour les prochains appels.
- La **clé de cache** est construite automatiquement depuis les paramètres de la méthode. Ici, chaque jour (`"lundi"`, `"mardi"`…) est une entrée distincte.

```java
@CacheResult(cacheName = "ardoise-menu")
public List<Plat> obtenirMenuDuJour(String jour) {
    // Ce code n'est exécuté que lors d'un cache miss
    return caveRepository.consulterGrandLivre(jour);
}
```

### `@CacheInvalidate(cacheName = "ardoise-menu")`

Efface une entrée précise. La clé est construite à partir des paramètres de la méthode annotée.

```java
@CacheInvalidate(cacheName = "ardoise-menu")
public void effacerMenuDuJour(String jour) {
    // Corps vide : l'annotation fait tout le travail
}
```

### `@CacheInvalidateAll(cacheName = "ardoise-menu")`

Efface toutes les entrées du cache d'un coup.

```java
@CacheInvalidateAll(cacheName = "ardoise-menu")
public void effacerTouteArdoise() {
    // Corps vide : l'annotation fait tout le travail
}
```

## Configuration (Caffeine)

Dans `application.properties` :

```properties
# Taille maximale de l'ardoise (entrées LRU évincées au-delà)
quarkus.cache.caffeine."ardoise-menu".maximum-size=100

# Durée de vie d'une inscription sur l'ardoise
quarkus.cache.caffeine."ardoise-menu".expire-after-write=10M
```

> Sans configuration explicite, le cache fonctionne quand même avec des valeurs par défaut
> (taille illimitée, pas d'expiration). La configuration permet de maîtriser la mémoire et
> de garantir la fraîcheur des données.

## Architecture du projet

```
cache-tutorial/
├── src/main/java/fr/eletutour/
│   ├── model/
│   │   └── Plat.java                  # Record immuable (nom, description, prix)
│   ├── repository/
│   │   └── CaveRepository.java        # Source de vérité "lente" (200 ms simulés)
│   ├── service/
│   │   └── MenuService.java           # ← L'ardoise : @CacheResult, @CacheInvalidate
│   └── resource/
│       └── MenuResource.java          # Comptoir : GET/DELETE /menu/{jour}
└── src/test/java/fr/eletutour/
    └── resource/
        └── MenuResourceTest.java      # Tests : cache miss/hit, invalidation, performance
```

## Endpoints REST

| Méthode | URL | Description | Comportement cache |
|---|---|---|---|
| `GET` | `/menu/{jour}` | Consulter le menu du jour | Cache hit ou miss |
| `DELETE` | `/menu/{jour}` | Effacer un jour de l'ardoise | Invalide 1 entrée |
| `DELETE` | `/menu` | Effacer toute l'ardoise | Invalide tout |

## Comment tester ?

```bash
# Lancer l'application
./mvnw quarkus:dev -pl core-concept/cache-tutorial

# Premier appel : cache miss → ~200 ms (descente à la cave)
curl http://localhost:8080/menu/lundi

# Deuxième appel : cache hit → < 1 ms (ardoise consultée)
curl http://localhost:8080/menu/lundi

# Effacer lundi de l'ardoise
curl -X DELETE http://localhost:8080/menu/lundi

# Premier appel après invalidation : cache miss à nouveau
curl http://localhost:8080/menu/lundi

# Effacer toute l'ardoise
curl -X DELETE http://localhost:8080/menu
```

L'interface **Swagger UI** est disponible sur : http://localhost:8080/q/swagger-ui

## Lancer les tests

```bash
./mvnw test -pl core-concept/cache-tutorial
```
