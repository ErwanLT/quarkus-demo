# Documentation d'API REST (OpenAPI)

Ce tutoriel montre comment exposer et documenter des APIs REST dans l'univers d'une **taverne medievale** via **SmallRye OpenAPI** et **Swagger UI**.

## Endpoints

- `GET /api/tavern/greeting?name=...`
- `POST /api/tavern/order`

## Documentation

- OpenAPI JSON : `http://localhost:8080/openapi`
- Swagger UI : `http://localhost:8080/q/swagger-ui`

## Configuration cle

```properties
quarkus.smallrye-openapi.path=/openapi
quarkus.swagger-ui.always-include=true
```

## Comment l'executer

```bash
mvn quarkus:dev
```

Exemples rapides :

```bash
curl "http://localhost:8080/api/tavern/greeting?name=Elora"
```

```bash
curl -X POST http://localhost:8080/api/tavern/order \
  -H "Content-Type: application/json" \
  -d '{"item":"Healing Potion","quantity":2}'
```
