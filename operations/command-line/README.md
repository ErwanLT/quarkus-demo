# Le Commis de Course

Squelette d'application Quarkus en ligne de commande, pensé pour la série *The Falling Whale*.

## La métaphore

La taverne n'a pas seulement besoin de serveurs au comptoir (REST/GraphQL) qui restent éveillés
toute la journée, ni d'un brasseur de nuit (Jakarta Batch) qui travaille jusqu'à l'aube. Elle a
parfois besoin d'un commis de course : un coursier de la guilde réveillé pour une mission
ultra-précise, qui s'exécute en une fraction de seconde, rend son rapport dans la console, et
repart aussitôt se coucher.

## Structure

```
src/main/java/fr/sfeir/taverne/commis/
├── Main.java                        # Point d'entree du processus (@QuarkusMain)
├── CommisShellApplication.java      # Boucle interactive : lit une ligne, l'execute, recommence
├── CommisDeCourseCommand.java       # Commande racine Picocli, liste les missions
├── domain/
│   └── DetteAventurier.java         # Objet du domaine (record)
├── service/
│   └── RegistreDettesService.java   # Logique métier, injectable et testable
└── mission/
    ├── ExporterDettesCommand.java   # Mission complète, avec service injecté
    ├── ReassortUrgentCommand.java   # Mission squelette à compléter
    └── PurgerGrimoireCommand.java   # Mission squelette à compléter
```

Chaque mission reste une sous-commande Picocli indépendante et injectable. Ce qui change par
rapport à une CLI classique : le processus ne s'arrête pas après une commande. `CommisShellApplication`
garde le commis éveillé dans une boucle, affiche une invite `commis>`, et exécute chaque ligne tapée
jusqu'à ce qu'on le renvoie se coucher (`quitter`, `exit`, ou `bonne-nuit`).

## Lancer le commis en mode dev

```bash
./mvnw quarkus:dev
```

Une invite `commis>` apparaît directement dans le terminal. Tape par exemple :

```
commis> help
commis> exporter-dettes
commis> exporter-dettes --critiques-seulement
commis> reassort-urgent houblon 50
commis> purger-grimoire --nom "Almanach des Tavernes" --simulation
commis> quitter
```

Note : en mode `quarkus:dev`, certaines touches sont normalement captées par la console de
live-reload de Quarkus. Si ça interfère avec la saisie, préfère le mode packagé ci-dessous pour
une vraie session interactive sans interférence.

## Construire et lancer un exécutable

```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

## Construire un exécutable natif

Le commis n'a pas vocation à traîner : un binaire natif GraalVM démarre en quelques millisecondes,
avant même d'afficher l'invite.

```bash
./mvnw package -Dnative
./target/commis-de-course-1.0.0-SNAPSHOT-runner
```

## Ajouter une nouvelle mission

1. Créer une classe dans `mission/`, annotée `@Command`, implémentant `Runnable` ou
   `Callable<Integer>`.
2. L'ajouter dans `subcommands` de `CommisDeCourseCommand`.
3. Injecter les services nécessaires via `@Inject`, comme dans `ExporterDettesCommand`.
