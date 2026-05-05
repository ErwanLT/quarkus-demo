# API Versioning Tutorial - Les Grimoires du Comptoir

Dans une taverne medievale facon DnD, les contrats d'API ressemblent a des grimoires : certains aventuriers lisent encore les vieilles runes, pendant que la guilde reclame une fiche plus detaillee.

Ce module montre quatre strategies courantes de versioning d'API REST avec Quarkus :

- **Path versioning** : la version est visible dans l'URL.
- **Parameter versioning** : la version est portee par un query parameter.
- **Header versioning** : la version est portee par un header HTTP dedie.
- **Content negotiation** : la version est portee par le media type demande via `Accept`.

## Contrats exposes

La V1 retourne un contrat volontairement simple :

```json
{
  "plat": "Ragout de sanglier",
  "prixPiecesCuivre": 12
}
```

La V2 enrichit le contrat sans casser les clients V1 :

```json
{
  "plat": "Ragout de sanglier aux herbes de druide",
  "prix": {
    "montant": 12,
    "devise": "pieces-cuivre"
  },
  "ingredients": ["sanglier", "orge", "carottes", "thym de druide"],
  "disponiblePour": "aventuriers niveau 2 et plus"
}
```

Les deux contrats implementent une `sealed interface MenuResponse`. Cela montre que Java peut modeliser une famille fermee de representations (`MenuV1Response` et `MenuV2Response`), tout en gardant deux payloads HTTP distincts.

```java
public sealed interface MenuResponse permits MenuV1Response, MenuV2Response {
    String plat();
}
```

Le controleur reste volontairement fin : il traduit HTTP vers le cas d'usage, tandis que `TavernMenuService` porte le choix metier de la version a servir. Les versions supportees sont modelisees par `MenuVersion`, ce qui evite de disperser les chaines `"1"` et `"2"` dans l'application.

## Endpoints

### 1. Version dans le path

```bash
curl http://localhost:8080/taverne/versioning/path/v1/menu
curl http://localhost:8080/taverne/versioning/path/v2/menu
```

Lisible, facile a router et tres explicite dans les logs. C'est souvent le choix le plus simple pour des APIs publiques.

### 2. Version dans un parameter

```bash
curl "http://localhost:8080/taverne/versioning/parameter/menu?version=1"
curl "http://localhost:8080/taverne/versioning/parameter/menu?version=2"
```

Pratique pour des tests ou des APIs internes, mais la version est moins visible dans certaines couches d'infrastructure.

### 3. Version dans un header

```bash
curl -H "X-Api-Version: 1" http://localhost:8080/taverne/versioning/header/menu
curl -H "X-Api-Version: 2" http://localhost:8080/taverne/versioning/header/menu
```

L'URL reste stable et le contrat evolue via metadata HTTP. C'est propre, mais il faut que les clients, proxies et outils de debug pensent bien a transmettre le header.

### 4. Content negotiation avec media type

```bash
curl -H "Accept: application/vnd.tavern.menu.v1+json" http://localhost:8080/taverne/versioning/negotiation/menu
curl -H "Accept: application/vnd.tavern.menu.v2+json" http://localhost:8080/taverne/versioning/negotiation/menu
```

Ici, la version fait partie du contrat de representation. C'est tres REST, notamment quand une meme ressource peut avoir plusieurs representations.

## Erreurs standardisees

Une version inconnue retourne **HTTP 400 Bad Request** avec un corps `application/problem+json` :

```bash
curl "http://localhost:8080/taverne/versioning/parameter/menu?version=dragon"
```

```json
{
  "type": "https://eletutour.fr/problems/api-version-unknown",
  "title": "Version d'API inconnue",
  "status": 400,
  "detail": "Le grimoire d'API ne connait que les versions 1 et 2.",
  "code": "VERSION_INCONNUE"
}
```

Les endpoints et DTOs sont documentes avec les annotations OpenAPI afin d'apparaitre clairement dans Swagger UI.

## Lancer le module

Depuis la racine du repository :

```bash
./mvnw -pl web-concept/api-versioning-tutorial quarkus:dev
```

## Lancer les tests

```bash
./mvnw -pl web-concept/api-versioning-tutorial -am test -DskipITs
```
