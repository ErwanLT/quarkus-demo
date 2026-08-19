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

## Tests

```bash
./mvnw test
```

Le test `RavenMasterServiceTest` utilise le connecteur `smallrye-in-memory` pour verifier
le comportement du maitre corbeau sans dependre d'un vrai broker Kafka.
