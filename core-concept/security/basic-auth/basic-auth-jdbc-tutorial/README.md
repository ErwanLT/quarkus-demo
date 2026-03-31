# Basic Auth JDBC - Taverne

Ce module montre comment securiser la reserve de nourriture et de biere d'une taverne avec **Basic Auth** et des utilisateurs stockes en base via **Elytron JDBC**.

## Endpoints securises

- `GET /api/tavern/pantry` (role `keeper`)
- `GET /api/tavern/cellar` (roles `keeper`, `supplier`)

## Utilisateurs (stockes en base)

- `keeper` / `keeper123` (acces reserve + cave)
- `supplier` / `supplier123` (acces cave)

## Comment l'executer

```bash
mvn quarkus:dev
```

## Tests rapides

```bash
curl -u keeper:keeper123 http://localhost:8080/api/tavern/pantry
```

```bash
curl -u supplier:supplier123 http://localhost:8080/api/tavern/cellar
```

Swagger UI est accessible sans authentification, mais l'execution des endpoints demandera une authentification Basic Auth.
