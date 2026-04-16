# Observabilite avancee avec Quarkus

Ce tutoriel couvre trois axes :
- /q/metrics et /q/health via Micrometer et SmallRye Health
- OpenTelemetry pour le tracing distribue
- Correlation des logs avec traceId et spanId
Une approche "pro" est proposee via un interceptor CDI `@BusinessTimed` pour factoriser les metriques metier.

## Lancer le module

```bash
./mvnw -pl operations/observabilite-advanced-tutorial quarkus:dev
```

## Lancer le collector OTEL (minimum syndical)

```bash
docker compose up -d
```
Pour verifier que le collector recoit des traces :
```bash
docker compose logs -f otel-collector
```

## UI de traces (Jaeger)

Une fois le compose lance, la UI Jaeger est disponible sur :
http://localhost:16686

## Endpoints utiles

- Metriques :
```bash
curl -s http://localhost:8080/q/metrics
```

- Health :
```bash
curl -s http://localhost:8080/q/health
```

- Appel applicatif (genere metrics + trace + log) :
```bash
curl -s "http://localhost:8080/advanced-tavern/order?drink=stout"
```

## Metriques metier (approche factorisee)

Le service est annote avec `@BusinessTimed`, ce qui produit :
- un timer `tavern.advanced.order` (expose comme `tavern_advanced_order_seconds_*`)
- un compteur `tavern.advanced.order.count` (expose comme `tavern_advanced_order_count_total`)

## Tests

```bash
./mvnw -pl operations/observabilite-advanced-tutorial test
```

## Notes de configuration

Les proprietes dans `application.properties` activent :
- Un export OTLP pour les traces vers `http://localhost:4317`
- Un format de log avec `traceId` et `spanId`
Le module utilise l'exporteur OTLP fourni par Quarkus, sans dependance additionnelle.

Pour visualiser les traces, branchez un collecteur OpenTelemetry et un backend (Jaeger, Tempo, etc.) sur le port OTLP.
Le collector minimal fourni utilise l'exporteur `debug` (le `logging` est deprecie).
