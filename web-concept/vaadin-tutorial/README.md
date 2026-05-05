# Vaadin avec Quarkus

Ce sous-module montre une integration Vaadin Flow dans Quarkus avec un decoupage explicite entre vues, service et repository.

## Lancer le projet

```bash
./mvnw -pl web-concept/vaadin-tutorial quarkus:dev
```

Puis ouvrir `http://localhost:8080/`.

## Points cles

- `com.vaadin:vaadin-quarkus-extension` active l'integration officielle Vaadin pour Quarkus.
- Le servlet `AdminServlet` mappe Vaadin sur `/*` pour exposer l'application a la racine du module.
- Le package `view` contient uniquement l'assemblage UI Vaadin.
- `TavernService` orchestre les donnees exposees aux vues.
- `InMemoryTavernRepository` centralise les jeux de donnees de demonstration.
- Le rendu visuel est compose cote composants pour garder un exemple simple a executer dans Quarkus sans pipeline frontend additionnel.
