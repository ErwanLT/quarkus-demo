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
