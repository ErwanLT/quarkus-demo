# Operations

Ce module regroupe les tutoriels orientes production et exploitation.

## [Observabilite - socle](observabilite-tutoriel)
Expose les metriques, la sante applicative, le tracing distribue et la correlation des logs.

## [Observabilite - avancee](observabilite-advanced-tutorial)
Etend le socle avec une stack de supervision complete :
- Prometheus pour le scraping et les regles d'alerte
- Grafana pour les dashboards
- Alertmanager pour la gestion des alertes

## [Tests de charge](load-testing-tutorial)
Tutoriel d'introduction aux tests de charge avec l'API Java de Gatling, avec le thème d'une Taverne Médiévale.
> **Note** : Ce module est exclu du build par défaut. Utilisez le profil `-Pload-testing` pour l'activer.

## [Batch processing - brassage de nuit](batch-processing-tutorial)
Simulation d'une journee de consommation puis reappro automatique des stocks la nuit via Quarkus Scheduler.

## [Command line - commis de course](command-line)
Application Quarkus en ligne de commande avec Picocli et JLine3.
Le commis expose un shell interactif, complete les missions au clavier et execute des commandes injectables comme `exporter-dettes`, `reassort-urgent` ou `purger-grimoire`.

## [Extension Quarkus personnalisée - rune de la taverne](quarkus-taverne-extension)
Tutoriel sur la création d'une extension Quarkus personnalisée (découpée en modules *runtime* et *deployment*) permettant de créer l'intercepteur `@Taverne` pour loguer et commenter l'exécution des méthodes annotées.

## [La taverne en flammes](la-taverne-en-flammes)
Resilience operationnelle de bout en bout : arret gracieux (`quarkus.shutdown.timeout` et sas de pre-arret),
sondes `@Liveness` / `@Readiness` pilotables, timeouts Mutiny et MicroProfile Fault Tolerance, vidange des
messages a l'arret, transactions JTA sous incident, runbook d'incident et post-mortem generes depuis l'etat reel.
