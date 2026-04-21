# i18n Tutorial - La Taverne Polyglotte

Ce module montre comment internationaliser une API REST Quarkus avec un univers de taverne DnD.

Le tutoriel combine :
- `@MessageBundle` (Qute) pour des messages typés et traduits.
- `Accept-Language` pour résoudre la locale côté serveur.
- Des exemples de l10n utiles en vrai : pluriels et monnaie.
- SmallRye Fault Tolerance (`@RateLimit`, `@Retry`, `@Fallback`) avec réponses localisées.

## Langues supportées

- `fr-FR` (par défaut)
- `en-US`
- `de-DE`
- `es-ES`
- `la-LA` (latin, avec règle spécifique pour la monnaie)

## Endpoints principaux

- `POST /taverne/commande` : commande une bière (limite 3 appels / 10s).
- `GET /taverne/cave` : tentative de descente à la cave avec retry.
- `GET /taverne/plat-du-jour` : fallback si la marmite est vide.
- `GET /taverne/accueil?nom=...` : message d'accueil localisé.
- `GET /taverne/prix?article=...&montant=...` : prix localisé.
- `GET /taverne/affluence?nombre=...` : pluriel localisé.

## Lancer et tester

1. Démarrer le module :

```bash
cd web-concept/i18n-tutorial
../../mvnw quarkus:dev
```

2. Tester la langue via `Accept-Language` :

```bash
curl -H "Accept-Language: en-US" "http://localhost:8080/taverne/accueil?nom=Thorin"
curl -H "Accept-Language: de-DE" "http://localhost:8080/taverne/commande" -X POST
curl -H "Accept-Language: la-LA" "http://localhost:8080/taverne/prix?article=potio&montant=3.5"
curl -H "Accept-Language: fr-FR" "http://localhost:8080/taverne/affluence?nombre=0"
```

## Tests

Depuis la racine du repository :

```bash
./mvnw -pl web-concept/i18n-tutorial -am test -DskipITs
```
