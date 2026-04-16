# Observabilite avancee avec Quarkus

Ce module etend le tutoriel observabilite avec une stack complete :
- Metrics et health via Quarkus (`/q/metrics`, `/q/health`)
- Tracing OTEL + Jaeger
- Prometheus pour la collecte
- Grafana pour les dashboards
- Alertmanager pour la gestion des alertes

## Demarrer le module Quarkus

```bash
./mvnw -pl operations/observabilite-advanced-tutorial quarkus:dev
```

## Demarrer la stack observabilite

```bash
docker compose up -d
```

## UIs disponibles

- Jaeger : http://localhost:16686
- Prometheus : http://localhost:9090
- Alertmanager : http://localhost:9093
- Grafana : http://localhost:3000 (admin/admin)

## Endpoints utiles

- Metrics Quarkus :
```bash
curl -s http://localhost:8080/q/metrics
```

- Health Quarkus :
```bash
curl -s http://localhost:8080/q/health
```

- Commande nominale :
```bash
curl -s "http://localhost:8080/advanced-tavern/order?drink=stout"
```

- Commande lente (pour tester l'alerte de latence) :
```bash
curl -s "http://localhost:8080/advanced-tavern/order?drink=dragonfire"
```

## Dashboard Grafana

Le dashboard `Tavern Advanced Overview` est provisionne automatiquement avec :
- Throughput des commandes
- Latence p95 des commandes
- Disponibilite de l'API

## Alerting

Les regles Prometheus sont dans `prometheus/alerts.yml`.

Alertes configurees :
- `TavernApiDown` : l'API ne repond plus depuis 30s
- `TavernOrderLatencyP95High` : la latence p95 depasse 500ms depuis 1 minute

### Scenario de test rapide

1. Generer du trafic nominal :
```bash
for i in {1..20}; do curl -s "http://localhost:8080/advanced-tavern/order?drink=stout" > /dev/null; done
```
2. Generer du trafic lent :
```bash
for i in {1..25}; do curl -s "http://localhost:8080/advanced-tavern/order?drink=dragonfire" > /dev/null; done
```
3. Verifier les alertes :
- Prometheus > Alerts : http://localhost:9090/alerts
- Alertmanager : http://localhost:9093

## Tests

```bash
./mvnw -pl operations/observabilite-advanced-tutorial test -DskipITs
```

## Notes

- Prometheus scrape l'application sur `host.docker.internal:8080/q/metrics`.
- Sur Linux, l'entree `host.docker.internal` est mappee via `extra_hosts: host-gateway` dans le compose.
