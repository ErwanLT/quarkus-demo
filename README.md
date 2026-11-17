[![Java CI with Maven](https://github.com/ErwanLT/quarkus-demo/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/ErwanLT/quarkus-demo/actions/workflows/maven.yml)
[![Java](https://img.shields.io/badge/Java-21-blue.svg?logo=java)](https://www.java.com)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.6-blue.svg?logo=quarkus)](https://quarkus.io)

# Quarkus Demo

Projet de démonstration des fonctionnalités et des intégrations possibles avec Quarkus.

## Sommaire

- [Démarrer ici](#démarrer-ici)
- [Core Concept](#core-concept-core-concept)
- [Web Concept](#web-concept-web-concept)
- [Operations](#operations-operations)
- [Database Access](#database-access-database-access)
- [Integration](#integration-integration)

## Démarrer ici

### Prérequis

| Outil | Version | Nécessaire pour |
|---|---|---|
| JDK | 21 | Tous les tutoriels |
| Maven | — | Inutile : le wrapper `./mvnw` est fourni |
| Docker | — | MongoDB, Kafka/Redpanda, la stack Prometheus/Grafana et Ollama (tutoriels LangChain4j) |

### Construire et tester l'ensemble

```bash
git clone https://github.com/ErwanLT/quarkus-demo.git
cd quarkus-demo
./mvnw clean verify
```

Le build complet local prend quelques minutes : chaque module est une application Quarkus indépendante. En CI, les cinq domaines sont construits en parallèle.

Le dossier `operations/quarkus-taverne-extension` est volontairement détaché du POM racine (une extension ne doit pas hériter des dépendances applicatives du mono-repo) : il se construit séparément, voir son [README](operations/quarkus-taverne-extension/README.md).

### Lancer un tutoriel

Chaque dossier de tutoriel est un module Maven du projet. Depuis la racine, en mode *live coding* :

```bash
./mvnw -pl web-concept/qute-tutorial quarkus:dev
```

Ou depuis le dossier du tutoriel, en pointant vers le wrapper de la racine (il n'y a pas de wrapper par module) :

```bash
cd web-concept/qute-tutorial
../../mvnw quarkus:dev
```

L'application est ensuite disponible sur <http://localhost:8080> (ou sur le port dédié indiqué dans le README du tutoriel). La Dev UI Quarkus est sur `/q/dev-ui`.

### Ports

La plupart des tutoriels utilisent le port `8080` par défaut : on en lance donc **un seul à la fois**. Certains modules déclarent un port dédié pour pouvoir cohabiter :

| Port | Tutoriel |
|---|---|
| `8081` | `web-concept/graphQL/federation-tutorial/aventurier-service` |
| `8082` | `web-concept/graphQL/federation-tutorial/quete-service`, `web-concept/qute-tutorial` |
| `8083` | `database-access/panache-tutorial` |
| `8084` | `database-access/flyway-liquibase-tutorial` |
| `8085` | `database-access/mongodb-tutorial` |
| `8086` | `operations/la-taverne-en-flammes` |
| `8087` | `integration/langchain4j-tutorial` (profil `dev`) |
| `8088` | `integration/rest-client-tutorial` |
| `9000` | `web-concept/graphQL/graphql-client-tutorial`, `graphql-client-advanced-tutorial` |

### Ce que la CI couvre — et ce qu'elle ne couvre pas

Le workflow GitHub Actions exécute `./mvnw -pl <domaine> -amd verify` en **mode JVM** sur les cinq domaines, en parallèle. L'extension détachée `operations/quarkus-taverne-extension` a son propre job, car elle ne fait pas partie du reactor racine. Le build natif GraalVM/Mandrel n'est **pas** exécuté en CI (temps de build et image builder) : il se lance localement, par exemple pour le tutoriel natif :

```bash
./mvnw -pl core-concept/native-tutorial verify -Dnative
```

## Catalogue des tutoriels

## Core Concept (`core-concept`)

### [Bannière Quarkus](core-concept/banner-tutorial)
[Article](https://www.sfeir.dev/back/comment-personnaliser-la-banniere-dun-projet-quarkus/)  
Tutoriel sur la désactivation et la personnalisation de la bannière de démarrage Quarkus.

### [Documentation d'API REST (OpenAPI)](core-concept/doc-tutorial)
[Article](https://www.sfeir.dev/back/mettre-en-place-la-documentation-openapi-dans-une-application-quarkus/)  
Tutoriel OpenAPI et Swagger UI avec une API de taverne médiévale.

### [Gestion d'exceptions](core-concept/exception-handling-tutorial)
[Article](https://www.sfeir.dev/back/cartographier-les-pieges-dun-donjon-industrialiser-la-gestion-des-erreurs-avec-quarkus/)  
Approche industrialisée de la gestion des erreurs avec Quarkus via des `ExceptionMapper` et le modèle `Problem`.

### [Security](core-concept/security)
Tutoriels de securite pour proteger les ressources sensibles de la taverne.
#### [Basic Auth](core-concept/security/basic-auth)
[Article](https://www.sfeir.dev/back/securiser-le-cellier-dune-taverne-avec-basic-auth-dans-quarkus/)  
Exemples Basic Auth pour proteger la reserve de nourriture et de biere.
#### [JWT](core-concept/security/jwt)
[Article](https://www.sfeir.dev/back/proteger-le-livre-de-comptes-et-le-coffre-fort-avec-jwt-dans-quarkus/)  
Exemple JWT pour proteger le livre de comptes et le coffre fort.

### [Cache - L'ardoise magique](core-concept/cache-tutorial)
[Article](https://www.sfeir.dev/back/lardoise-magique-au-dessus-du-comptoir-utilisation-du-cache-dans-quarkus/)  
Démonstration de `quarkus-cache` avec `@CacheResult`, `@CacheInvalidate` et `@CacheInvalidateAll`.
Le tavernier lève les yeux vers son ardoise magique (le cache mémoire) plutôt que de redescendre
à la cave consulter le grand livre à chaque commande d'un aventurier.

### [Natif - Le barde figé dans la pierre](core-concept/native-tutorial)
*Article à venir.*  
Compilation native avec GraalVM/Mandrel : `-Dnative`, build conteneurisé, `@RegisterForReflection`,
`quarkus.native.resources.includes`, `--initialize-at-run-time` et `@QuarkusIntegrationTest`.
La statue du barde chante en 30 ms, là où le barde de chair met 600 ms à s'accorder.

## Web Concept (`web-concept`)

### [Qute Tutorial](web-concept/qute-tutorial)
[Article](https://www.sfeir.dev/back/qute-le-scribe-du-donjon-pour-afficher-la-taverne-avec-quarkus/)  
Tutoriel sur le moteur de template **Qute**, l'alternative moderne et optimisée de Quarkus à Thymeleaf, intégrant la gestion des chambres et du registre de la taverne **The Falling Whale**.

### [Rate Limiting (Taverne DnD)](web-concept/rate-limiting-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-sous-controle-maitriser-les-flux-avec-quarkus/)  
Tutoriel sur l'annotation `@RateLimit` de SmallRye Fault Tolerance pour limiter l'abus de commandes à la taverne.

### [GraphQL (Taverne DnD)](web-concept/graphQL)
Tutoriel complet sur l'utilisation de GraphQL avec Quarkus.
#### [GraphQL Serveur](web-concept/graphQL/graphql-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-graphql-decouvrir-graphql-avec-quarkus/)  
[Article - arts avancés du comptoir](https://www.sfeir.dev/back/la-taverne-graphql-maitriser-les-arts-avances-du-comptoir/)  
Exposition de données via MicroProfile GraphQL et gestion du registre des aventuriers, puis pagination (offset et curseur), directives personnalisées et résolution du N+1 par batch.
#### [GraphQL Client TypeSafe](web-concept/graphQL/graphql-client-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-graphql-consommer-une-api-graphql-avec-quarkus/)  
Consommation de l'API via SmallRye GraphQL TypeSafe Client.
#### [GraphQL Client Advanced](web-concept/graphQL/graphql-client-advanced-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-graphql-aller-plus-loin-avec-le-client-quarkus/)  
Utilisation du Dynamic Client et du Generator SmallRye.
#### [GraphQL Fédération](web-concept/graphQL/federation-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-graphql-un-royaume-de-tavernes/)  
Découpage du domaine en subgraphs Quarkus fédérés (`aventurier-service`, `quete-service`), composés et routés par Apollo Router.

### [i18n & l10n (Taverne DnD)](web-concept/i18n-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-multilingue/)  
Tutoriel sur l'internationalisation avec Qute MessageBundle et `Accept-Language`, incluant la localisation des messages, des montants et des pluriels.

### [API Versioning (Taverne DnD)](web-concept/api-versioning-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-aux-grimoires-versionner-une-api-rest-avec-quarkus/)  
Tutoriel sur le versioning d'API REST avec Quarkus : path, query parameter, header HTTP et content negotiation via media type.

### [Programmation reactive (Taverne DnD)](web-concept/reactive-programming-tutorial)
[Article](https://www.sfeir.dev/back/le-tavernier-aux-huit-bras-la-programmation-reactive-vue-depuis-le-comptoir/)  
Tutoriel Mutiny avec le tavernier aux huit bras : `Uni` pour une chope servie plus tard, `Multi` pour une file de clients en flux continu, et comparaison avec l'imperatif bloquant.

### [Vaadin Tutorial](web-concept/vaadin-tutorial)
[Article](https://www.sfeir.dev/back/dans-les-coulisses-de-the-falling-whale-securite-push-et-javascript/)  
Tutoriel sur l'intégration de **Vaadin Flow** pour construire des interfaces d'administration riches. Inclut une démonstration d'**intégration JavaScript personnalisée** (Leaflet pour les cartes, Chart.js pour les graphiques) pour s'affranchir des composants payants tout en gardant un thème médiéval immersif.

### [Éditique (L'imprimerie de la taverne)](web-concept/editique-tutorial)
[Article](https://www.sfeir.dev/back/limprimerie-de-la-taverne-tickets-et-factures-avec-jasperreports-et-quarkus/)  
Tutoriel sur la génération de documents avec **JasperReports 7** via l'extension Quarkiverse `quarkus-jasperreports` (rapports compilés au build) : ticket de caisse thermique 80 mm, facture fournisseur A4 et relevé mensuel des fournisseurs, avec sous-rapports (paramètres, valeur de retour), composant `list`, logo de la taverne, export PDF/XLSX et vérification des PDF avec PDFBox. Codes-barres et QR codes (Code 128, EAN-13, QR) sur les tickets, factures, étiquettes produits et cartes de table, avec pages de suivi Qute et relecture des codes par ZXing. Charte graphique commune en feuilles de styles `.jrtx` (palette, typographie, formats de montant, déclinaison thermique), verrouillée par des tests sur les sources et sur les couleurs des PDF.

## Operations (`operations`)

### [Observabilite - socle](operations/observabilite-tutoriel)
[Article](https://www.sfeir.dev/back/observer-la-taverne-en-pleine-effervescence-observabilite-avec-quarkus/)  
Tutoriel de base pour exposer metriques, health checks, tracing OpenTelemetry et correlation des logs.

### [Observabilite - avancee](operations/observabilite-advanced-tutorial)
[Article](https://www.sfeir.dev/back/quand-la-taverne-monte-en-charge-prometheus-grafana-et-alerting-avec-quarkus/)  
Extension orientee exploitation avec Prometheus, Grafana, Alertmanager et scenario d'alerting de bout en bout.

### [Tests de Charge (Gatling)](operations/load-testing-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-a-lepreuve-du-siege-tester-la-resilience-avec-gatling/)  
Tutoriel sur la mise en place de tests de charge avec Gatling (Java DSL) pour stresser le tavernier et vérifier la résilience de l'application. (Nécessite le profil `-Pload-testing`).

### [Batch Processing (Brassage de nuit)](operations/batch-processing-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-nocturne-traiter-les-flux-avec-quarkus-batch/)  
Tutoriel sur le batch processing avec Quarkus Scheduler pour brasser automatiquement la nuit les bieres consommees en journee.

### [Command Line (Commis de course)](operations/command-line)
[Article](https://www.sfeir.dev/back/le-commis-de-course-creer-une-cli-interactive-avec-quarkus/)  
[Article - donnez vie à vos CLI](https://www.sfeir.dev/back/donnez-vie-a-vos-cli-avec-quarkus/)  
Tutoriel sur la creation d'une application Quarkus en ligne de commande avec Picocli et JLine3, sous forme de shell interactif pour lancer des missions de taverne.

### [Extension Quarkus personnalisée (Rune de la Taverne)](operations/quarkus-taverne-extension)
[Article](https://www.sfeir.dev/back/quarkus-la-forge-aux-extensions/)  
Tutoriel sur la création d'une extension Quarkus personnalisée (découpée en modules *runtime* et *deployment*) pour intercepter et loguer des méthodes annotées avec `@Taverne`.

### [La Taverne en Flammes (Résilience & reprise)](operations/la-taverne-en-flammes)
[Article](https://www.sfeir.dev/back/la-taverne-en-flammes-un-manuel-de-resilience-operationnelle-pour-quarkus/)  
Tutoriel sur la résilience opérationnelle : arrêt gracieux, sondes `@Liveness` / `@Readiness` pilotables à chaud,
timeouts réactifs (Mutiny) et déclaratifs (`@Timeout`, `@Retry`, `@Fallback`), vidange des messages à l'arrêt,
transactions JTA sous incident, runbook d'incident et post-mortem sans blâme. On met le feu à la taverne via l'API
et on observe chaque mécanisme réagir.

## Database Access (`database-access`)

### [Hibernate Panache](database-access/panache-tutorial)
[Article](https://www.sfeir.dev/back/la-taverne-panache-persister-les-recettes-avec-quarkus/)  
Tutoriel sur l'accès aux données SQL avec Hibernate Panache, illustrant les patterns Active Record et Repository via le Grimoire des Recettes de la Taverne.

### [Le registre des travaux (Flyway & Liquibase)](database-access/flyway-liquibase-tutorial)
*Article à venir.*  
Tutoriel comparatif sur les **migrations de schéma** : scripts Flyway et changeSets Liquibase sur deux bases H2, adoption d'une base existante (baseline / changelogSync), triptyque expand/backfill/contract et validation du schéma par Hibernate Panache.

### [Les archives de la Guilde (MongoDB)](database-access/mongodb-tutorial)
*Article à venir.*  
Persistance document avec **MongoDB et Panache** : un dossier d'aventurier par document, prouesses intégrées en sous-documents, requêtes à points sur un tableau imbriqué, pagination, index unique, pipeline d'agrégation et Dev Services (conteneur `mongo:7`).

## Integration (`integration`)

### [Le coursier de la taverne (REST Client)](integration/rest-client-tutorial)
*Article à venir.*  
Client REST déclaratif (`@RegisterRestClient`, `configKey`, `@ClientHeaderParam`) et programmatique (`QuarkusRestClientBuilder`) : paramètres de chemin et de requête, en-têtes, timeouts, traduction des erreurs HTTP en `problem+json` et tests sans dépendance externe.

### [Messaging](integration/messaging)
Tutoriels d'integration autour des echanges asynchrones et des flux d'evenements.
#### [Server-Sent Events (Panneau magique)](integration/messaging/sse-tutorial)
[Article](https://www.sfeir.dev/back/le-tableau-daffichage-de-la-salle-commune-sse-avec-quarkus/)  
Tutoriel SSE avec RESTEasy Reactive et Mutiny pour diffuser les annonces du barman en temps reel vers les aventuriers connectes.
#### [Corbeaux messagers (Kafka)](integration/messaging/kafka-tutorial)
[Article](https://www.sfeir.dev/back/la-voliere-aux-corbeaux-messagers-introduction-a-kafka-dans-quarkus/)  
[Article - Retry et Dead Letter Queue](https://www.sfeir.dev/back/le-filet-de-secours-de-la-voliere-retry-et-dead-letter-queue/)  
Tutoriel d'intégration avec Apache Kafka (Redpanda) et SmallRye Reactive Messaging, incluant les mécanismes de tentatives (**Retry**) et de redirection vers la **Dead Letter Queue (DLQ)**.

#### [L'Oracle au fond de la salle (LangChain4j)](integration/langchain4j-tutorial)
Le miroir magique du barde : `quarkus-langchain4j`, service IA injectable avec `@RegisterAiService`, tool calling vers Panache et RAG sur le grimoire des recettes.
