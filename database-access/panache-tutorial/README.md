# Hibernate Panache Tutorial - Le Grimoire des Recettes

Ce module démontre comment utiliser **Hibernate Panache** pour gérer la persistance des données dans une application Quarkus.

## Concepts Clés

### 1. Active Record Pattern (`PanacheEntity`)
L'entité porte elle-même les méthodes d'accès aux données. C'est l'approche la plus concise pour les modèles simples.

### 2. Repository Pattern (`PanacheRepository`)
Une classe séparée gère l'accès aux données, ce qui permet une meilleure séparation des responsabilités pour les modèles complexes.

### 3. Requêtes Simplifiées
Utilisation du langage de requête de Panache pour effectuer des recherches, des tris et de la pagination de manière intuitive.

## Thématique : Le Grimoire des Recettes de la Taverne

Nous allons modéliser :
- Des **Ingrédients** (Active Record)
- Des **Recettes** (Repository)

## Lancer le module

```bash
./mvnw -pl database-access/panache-tutorial quarkus:dev
```
