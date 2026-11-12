# Les corbeaux messagers de la taverne (Kafka + Quarkus)

Petit projet de demo pour l'article Kafka/Quarkus, filant la metaphore des
corbeaux messagers :

- `RavenMessage` : le parchemin attache a la patte du corbeau (le payload Kafka).
- `RavenMasterService` : le maitre corbeau, qui lache le message sur le topic `raven-roost` (producer).
- `RavenRoostListener` : le colombier, qui receptionne les corbeaux et lit leur message (consumer).
- `RavenResource` : le comptoir de la taverne, ou l'on vient demander l'envoi d'un corbeau (endpoint REST).

## Lancer en local

```bash
./mvnw quarkus:dev
```

Quarkus Dev Services demarre automatiquement un broker Kafka pour le mode dev, rien a configurer.

## Depecher un corbeau

```bash
curl -X POST http://localhost:8080/ravens \
  -H "Content-Type: application/json" \
  -d '{"sender": "Tavernier", "content": "La biere est arrivee"}'
```

Le log du colombier (`RavenRoostListener`) doit afficher la reception du message quelques instants plus tard.

### Tester la gestion des erreurs (Retry & DLQ)

Pour tester le mécanisme de retry et de Dead Letter Queue (DLQ), vous pouvez envoyer un message contenant le mot `"erreur"` ou `"fail"` :

```bash
curl -X POST http://localhost:8080/ravens \
  -H "Content-Type: application/json" \
  -d '{"sender": "Tavernier", "content": "Ce message contient une erreur fatale"}'
```

Le traitement de ce message dans `RavenRoostListener` va échouer. Grâce à la configuration :
1. Le listener va retenter le traitement **jusqu'à 3 fois** (avec 1 seconde d'intervalle). Les tentatives de retry s'affichent dans les logs.
2. Après ces échecs, le message est rejeté (`nack`) et envoyé automatiquement sur le topic de DLQ `raven-roost-dlq`.

Pour consulter les messages arrivés dans la DLQ (quand Quarkus Dev Services tourne) :
```bash
docker exec -it $(docker ps -q --filter ancestor=redpandadata/redpanda) rpk topic consume raven-roost-dlq
```

## Tests

```bash
./mvnw test
```

Les tests n'ont besoin ni de broker Kafka ni de Docker : les canaux passent par le connecteur `smallrye-in-memory`, ce qui leur permet de tourner tels quels en CI.
- `RavenMasterServiceTest` : vérifie que le maître corbeau publie bien le message sur le canal sortant.
- `RavenRetryDlqTest` : pose des corbeaux directement au colombier. Un message sain est acquitté (`ack`) ; un message en erreur est retenté puis rejeté (`nack`), ce qui déclenche, avec le vrai connecteur Kafka, l'envoi vers la DLQ. Le test vérifie aussi que la stratégie `dead-letter-queue` pointe bien sur `raven-roost-dlq`. Le routage vers la DLQ est assuré par le connecteur Kafka lui-même : on le constate en dev avec la commande `rpk` ci-dessus.

