# Tests de Charge avec Gatling

Ce tutoriel démontre comment mettre en place et exécuter des tests de charge pour une application Quarkus à l'aide de **Gatling** (avec l'API Java).

Plongé dans l'univers d'une **Taverne Médiévale (Donjons & Dragons)**, ce module simule le comportement d'une vague d'aventuriers venant tester la robustesse du tavernier (limitation de fréquence / Rate Limiting, Retry et Fallback).

## Prérequis

Avant de lancer les tests de charge, vous devez démarrer l'application cible (la taverne). Dans un terminal séparé à la racine du projet, lancez :

```bash
./mvnw quarkus:dev -pl web-concept/rate-limiting-tutorial
```

## Lancer les tests

Pour exécuter la simulation de test de charge (100 aventuriers sur 15 secondes), lancez la commande suivante (une fois que la taverne est prête à accueillir des clients) :

```bash
./mvnw clean gatling:test -pl operations/load-testing-tutorial -Pload-testing
```

Le rapport de test sera généré dans le dossier `target/gatling/`. Vous pourrez l'ouvrir avec un simple navigateur web pour visualiser les graphiques interactifs des performances du tavernier !

## Endpoints testés

La simulation `TavernLoadTest` teste les endpoints suivants (exposés idéalement par le module `rate-limiting-tutorial`) :
- `POST /taverne/commande` : L'aventurier commande une bière (Sensible au *Rate Limiting* ! Un 429 est tout à fait acceptable en cas de burst).
- `GET /taverne/cave` : Le tavernier descend chercher une bouteille dans la cave (Sensible au *Retry*).
- `GET /taverne/plat-du-jour` : L'aventurier commande le ragoût / plat du jour (Sensible au *Fallback*).
