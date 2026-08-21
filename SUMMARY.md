# Resume des Tutoriels par Categorie

## Core Concept (`core-concept`)
- Customisation banniere (Quarkus)
- Documentation d'API REST (OpenAPI)
- Gestion centralisée des exceptions (RFC 9457 / ProblemDetail)
- Securite
  - Basic Auth
    - Utilisateurs embarques (embedded)
    - Utilisateurs en base via JDBC (Elytron JDBC)
    - Utilisateurs en base via JPA (security-jpa)
  - JWT
    - JWT classique
    - JWT + JPA (login + generation de token)
- Mise en cache (L'ardoise magique - `@CacheResult`, `@CacheInvalidate`)

## Web Concept (`web-concept`)
- Qute (templates, layout, formulaires, Panache)
- Rate limiting (Taverne DnD - `@RateLimit`)
- GraphQL
  - Serveur (MicroProfile GraphQL)
  - Client TypeSafe (SmallRye)
  - Client Dynamique & Generateur
- Internationalisation & Localisation (i18n/l10n)
- Versioning d'API REST (path, parameter, header, media type)
- Programmation reactive (Mutiny - `Uni`, `Multi`)
- Vaadin Flow (UI d'administration, composants, simulation temps reel)

## Operations (`operations`)
- Observabilite (metrics, tracing, log correlation)
- Tests de charge (Gatling Java DSL)
- Batch processing (brassage nocturne multi-bieres)
- Command line (Picocli, JLine3, shell interactif)
- Extension Quarkus personnalisée (intercepteur `@Taverne` avec modules runtime/deployment)
- Resilience et reprise (arret gracieux, sondes de sante, timeouts, drain, JTA, runbook, post-mortem)

## Database Access (`database-access`)
- Hibernate Panache (Active Record vs Repository)

## Integration (`integration`)
- Messaging
  - Server-Sent Events (RESTEasy Reactive, Mutiny, flux `text/event-stream`)
  - Kafka (Volière aux corbeaux, Retry et Dead Letter Queue / DLQ)

# Sujets non encore traites (Roadmap)

## Core Concept
- Validation
- Configuration et profils
- Quarkus Native (GraalVM)

## Database Access
- Flyway / Liquibase
- Reactive SQL (R2DBC)
- MongoDB
- Multi-tenancy

## Integration
- Messaging avance (JMS)
- gRPC
- WebSockets
- REST Client (MicroProfile Rest Client)
- IA (RAG, tools)

## Operations
- Logs structures
- CI/CD
- Docker / Kubernetes

## Web Layer
- Upload / Download de fichiers
