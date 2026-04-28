[![Java](https://img.shields.io/badge/Java-25-blue.svg?logo=java)](https://www.java.com)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.1-blue.svg?logo=quarkus)](https://quarkus.io)

# Quarkus Demo

Projet de démonstration des fonctionnalités et des intégrations possibles avec Quarkus.

## Core Concept (`core-concept`)

### [Bannière Quarkus](core-concept/banner-tutorial)
Tutoriel sur la désactivation et la personnalisation de la bannière de démarrage Quarkus.

### [Documentation d'API REST (OpenAPI)](core-concept/doc-tutorial)
Tutoriel OpenAPI et Swagger UI avec une API de taverne médiévale.

### [Gestion d'exceptions](core-concept/exception-handling-tutorial)
Approche industrialisée de la gestion des erreurs avec Quarkus via des `ExceptionMapper` et le modèle `Problem`.

### [Security](core-concept/security)
Tutoriels de securite pour proteger les ressources sensibles de la taverne.
#### [Basic Auth](core-concept/security/basic-auth)
Exemples Basic Auth pour proteger la reserve de nourriture et de biere.
#### [JWT](core-concept/security/jwt)
Exemple JWT pour proteger le livre de comptes et le coffre fort.

## Web Concept (`web-concept`)

### [Qute Tutorial](web-concept/qute-tutorial)
Tutoriel sur le moteur de template **Qute**, l'alternative moderne et optimisée de Quarkus à Thymeleaf, intégrant la gestion des chambres et du registre de la taverne **The Falling Whale**.

### [Rate Limiting (Taverne DnD)](web-concept/rate-limiting-tutorial)
Tutoriel sur l'annotation `@RateLimit` de SmallRye Fault Tolerance pour limiter l'abus de commandes à la taverne.

### [GraphQL Tutorial (Taverne DnD)](web-concept/graphql-tutorial)
Tutoriel sur l'exposition de données via GraphQL, incluant la gestion du registre des aventuriers et des quêtes avec une architecture industrialisée.

### [i18n & l10n (Taverne DnD)](web-concept/i18n-tutorial)
Tutoriel sur l'internationalisation avec Qute MessageBundle et `Accept-Language`, incluant la localisation des messages, des montants et des pluriels.

## Operations (`operations`)

### [Observabilite - socle](operations/observabilite-tutoriel)
Tutoriel de base pour exposer metriques, health checks, tracing OpenTelemetry et correlation des logs.

### [Observabilite - avancee](operations/observabilite-advanced-tutorial)
Extension orientee exploitation avec Prometheus, Grafana, Alertmanager et scenario d'alerting de bout en bout.

### [Tests de Charge (Gatling)](operations/load-testing-tutorial)
Tutoriel sur la mise en place de tests de charge avec Gatling (Java DSL) pour stresser le tavernier et vérifier la résilience de l'application. (Nécessite le profil `-Pload-testing`).

### [Batch Processing (Brassage de nuit)](operations/batch-processing-tutorial)
Tutoriel sur le batch processing avec Quarkus Scheduler pour brasser automatiquement la nuit les bieres consommees en journee.
