# Server-Sent Events - Panneau magique

Ce module montre comment exposer un flux **Server-Sent Events** avec Quarkus RESTEasy Reactive et Mutiny.

Dans la taverne, les aventuriers observent un panneau magique. Le barman poste une annonce, et tous les clients connectes la recoivent immediatement sur un flux `text/event-stream`.

## Concepts illustres

- `@Produces(MediaType.SERVER_SENT_EVENTS)` pour exposer un endpoint SSE.
- `@RestStreamElementType(MediaType.APPLICATION_JSON)` pour envoyer des evenements JSON.
- `Multi<Annonce>` comme flux reactive cote serveur.
- `BroadcastProcessor` pour diffuser une annonce a tous les abonnes.
- Filtre JAX-RS pour forcer le charset UTF-8 sur le flux SSE.

## Endpoints

- `GET /taverne/panneau` : ouvre le flux SSE des annonces.
- `POST /taverne/crier` : publie une nouvelle annonce sur le panneau.

## Comment l'executer

Depuis la racine du projet :

```bash
./mvnw -pl integration/messaging/sse-tutorial quarkus:dev
```

## Tests rapides

Dans un premier terminal, ouvrir le flux :

```bash
curl -N http://localhost:8080/taverne/panneau
```

Dans un second terminal, publier une annonce :

```bash
curl -X POST http://localhost:8080/taverne/crier \
  -H "Content-Type: application/json" \
  -d '{"message":"Tournee generale dans la salle commune !"}'
```

Le premier terminal recoit alors un evenement JSON contenant l'auteur, le message et l'horodatage.
