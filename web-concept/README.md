# Web Concept

Ce module regroupe les tutoriels sur les concepts Web de Quarkus.

## [Qute Tutorial](qute-tutorial)
Tutoriel sur le moteur de template **Qute**, l'alternative moderne et optimisée de Quarkus à Thymeleaf.
Démonstration avec l'interface de la taverne **The Falling Whale** incluant :
*   Le rendu de templates HTML type-safe.
*   L'utilisation de layouts partagés.
*   La gestion des formulaires et de la persistance avec Hibernate Panache.

## [Rate Limiting](rate-limiting-tutorial)
Tutoriel sur la gestion du débit des requêtes avec SmallRye Fault Tolerance.
Démonstration dans la Taverne avec la mécanique du `@RateLimit` pour éviter que les nains ne dévalisent les tonneaux trop vite.

## [GraphQL Tutorial](graphql-tutorial)
Tutoriel sur l'exposition de données via GraphQL (MicroProfile GraphQL).
Démonstration avec le registre des aventuriers, incluant l'utilisation de DTOs (Records), de la couche Service et des contraintes de non-nullité.

## [i18n Tutorial](i18n-tutorial)
Tutoriel sur l'internationalisation et la localisation d'une API REST Quarkus.
Démonstration dans la Taverne avec la résolution de langue via `Accept-Language`, des traductions multi-langues (fr, en, de, es, la), et des cas l10n (monnaie, pluriels).
