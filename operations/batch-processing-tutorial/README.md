# Batch processing - Brassage de nuit (Taverne DnD)

Ce tutoriel montre un batch nocturne avec **Quarkus JBeret**.

Idee:
- la journee, la taverne sert plusieurs types de bieres,
- la nuit, un job batch re-brasse automatiquement ce qui a ete consomme.

## Stack utilisee

- `io.quarkiverse.jberet:quarkus-jberet` pour le job batch (JSR-352)
- `quarkus-scheduler` pour declencher le job tous les soirs
- OpenAPI pour documenter les endpoints
- Problem Details (RFC 7807) pour standardiser les erreurs

Le job est defini dans `src/main/resources/META-INF/batch-jobs/nightly-brew-job.xml`.

## Lancement en dev

```bash
./mvnw -pl operations/batch-processing-tutorial quarkus:dev
```

## Cron nocturne

```properties
tavern.brewery.nightly-cron=0 0 2 * * ?
```

Par defaut: execution a 02:00.

## API REST

- Consulter les stocks :
```bash
curl -s http://localhost:8080/tavern/brewery/stocks
```

- Enregistrer la consommation de journee :
```bash
curl -s -X POST http://localhost:8080/tavern/brewery/day-consumptions \
  -H 'Content-Type: application/json' \
  -d '{
    "orders": {
      "DRAGON_STOUT": 30,
      "ELVEN_IPA": 20,
      "DWARVEN_LAGER": 15
    }
  }'
```

- Lancer le lot de brassage nocturne :
```bash
curl -s -X POST http://localhost:8080/tavern/brewery/nightly-batches
```

- Reinitialiser la simulation (idempotent) :
```bash
curl -s -X PUT http://localhost:8080/tavern/brewery/stocks/reset
```

## OpenAPI

- Swagger UI: http://localhost:8080/q/swagger-ui
- Spec JSON: http://localhost:8080/q/openapi

## Tests

```bash
./mvnw -pl operations/batch-processing-tutorial -am test
```

Les tests valident:
- la consommation journee,
- le lancement du job JBeret,
- le statut `COMPLETED`,
- la remise a niveau des stocks apres brassage,
- les erreurs d'entree avec Problem Details.
