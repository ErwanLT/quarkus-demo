# JWT + JPA - Taverne

Ce module montre comment securiser le **livre de comptes** et le **coffre fort** avec **JWT**, en stockant les utilisateurs en base via **JPA**.

## Endpoints

- `POST /api/tavern/auth/login`
- `GET /api/tavern/ledger` (role `accountant`)
- `GET /api/tavern/vault` (role `treasurer`)

## Utilisateurs de demo

- `elora` / `ledger123` (role `accountant`)
- `borin` / `vault123` (role `treasurer`)

## Comment l'executer

```bash
mvn quarkus:dev
```

## Tests rapides

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/tavern/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"elora","password":"ledger123"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/tavern/ledger
```

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/tavern/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"borin","password":"vault123"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/tavern/vault
```

Notes:
- Les mots de passe sont hashes en bcrypt avant sauvegarde (demo).
- La cle privee est embarquee uniquement pour signer les JWT localement.
