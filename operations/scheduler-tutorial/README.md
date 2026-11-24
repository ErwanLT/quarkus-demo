# Les corvées de la Taverne Nocturne : `@Scheduled` avec Quarkus

*La métaphore : dans « La Taverne Nocturne : traiter les flux avec Quarkus Batch », l'intendant
s'enferme la nuit avec des piles de registres, un `ItemReader`, des checkpoints et une reprise sur
erreur. Mais la taverne ne vit pas que la nuit. Le valet, lui, vérifie la pression des fûts toutes
les dix minutes, ramasse les verres toutes les heures, balaye l'entrée à l'aube et réveille
l'intendant à trois heures du matin. Ce sont des corvées : petites, fréquentes, déclenchées par
l'horloge. C'est le rôle de `@Scheduled`.*

Ce module est la suite de [Batch processing (brassage de nuit)](../batch-processing-tutorial). Il
montre `quarkus-scheduler` :

- la complémentarité **Batch vs Scheduler** et le pont entre les deux ;
- `every` (durée) vs `cron` (instant du calendrier), avec des cadences configurables ;
- l'exécution **bloquante par défaut** et le passage à la boucle d'événements avec `@NonBlocking` ;
- `ScheduledExecution`, les horodatages *prévu / réel* et `concurrentExecution = SKIP` ;
- l'observation et le pilotage des tâches via l'API `Scheduler` ;
- le piège du cluster avec trois répliques, et ce qu'apporte `quarkus-quartz`.

## Lancer le module

```bash
./mvnw -pl operations/scheduler-tutorial quarkus:dev
```

L'application écoute sur le port **8089**.

| Ressource | URL |
| :--- | :--- |
| Swagger UI | <http://localhost:8089/q/swagger-ui> |
| État des corvées | <http://localhost:8089/corvees> |
| Tâches enregistrées par Quarkus | <http://localhost:8089/corvees/taches> |

```bash
curl -s localhost:8089/corvees
curl -s localhost:8089/corvees/taches

# Forcer une corvée sans attendre son échéance
curl -s -X POST localhost:8089/corvees/PRESSION
curl -s -X POST localhost:8089/corvees/INTENDANT
```

En mode *dev*, ajustez les cadences dans `application.properties` : le rechargement à chaud
redémarre le scheduler avec les nouvelles valeurs. Par exemple `corvees.pression.every=5s` permet
de voir la corvée se répéter sous vos yeux.

## 1. Batch et Scheduler ne répondent pas à la même question

| | Batch (jberet / JBatch) | Scheduler (`@Scheduled`) |
| :--- | :--- | :--- |
| Question | « Comment transformer 100 000 lignes sans exploser la mémoire et en reprenant après un crash ? » | « Comment déclencher une action récurrente à un instant T ? » |
| Unité de travail | Un *job*, découpé en *steps* et en *chunks* | Une méthode, une corvée |
| Axe | **Données** et transactions | **Temps** et cadence |
| État | Persisté dans les tables JBatch, redémarrable | En mémoire dans l'instance (par défaut) |
| Reprise après incident | Checkpoints, `JobOperator.restart()` | Aucune : une échéance manquée est manquée |
| Méthode typique | `ItemReader` → `ItemProcessor` → `ItemWriter` | `@Scheduled(cron = "...")` |
| À la taverne | L'intendant clôture le grand livre de la nuit | Le valet vérifie la pression des fûts |

Les deux ne se remplacent pas : le scheduler **déclenche**, le batch **traite**. Et le pont est
naturel — qui réveille le job batch à 3 h du matin ? Une méthode annotée `@Scheduled` qui appelle
`JobOperator.start(...)`. Le module batch contient déjà son propre `NightlyBrewScheduler` : ce
module montre le même geste côté déclencheur, sans dépendre du module batch.

## 2. Cinq corvées, deux façons de dire « quand »

| Corvée | Déclenchement | Expression | Fil d'exécution |
| :--- | :--- | :--- | :--- |
| Pression des fûts | Toutes les 10 minutes | `every = "${corvees.pression.every:10m}"` | Pool de threads (par défaut) |
| Verres vides | Toutes les heures | `every = "${corvees.verres.every:1h}"`, `concurrentExecution = SKIP` | Pool de threads |
| Balayage de l'aube | 6 h 30 | `cron = "${corvees.balayage.cron:0 30 6 * * ?}"` | Pool de threads |
| Nettoyage du comptoir | Toutes les 30 secondes | `every = "${corvees.comptoir.every:30s}"`, `@NonBlocking` | Boucle d'événements Vert.x |
| Réveil de l'intendant | 3 h 00 | `cron = "${corvees.batch.cron:0 0 3 * * ?}"` | Pool de threads |

`every` est une **durée relative** : `30s`, `10m`, `1h`. Elle s'exécute une période après la
précédente (ou après le démarrage). `cron` est un **instant absolu** dans le calendrier : Quarkus
utilise par défaut la syntaxe Quartz à six ou sept champs, avec les **secondes en premier** et un
`?` pour le jour du mois ou de la semaine. `0 0 3 * * ?` signifie donc « tous les jours à 03:00:00 ».

Les deux attributs acceptent une expression de configuration. Ici on utilise la forme
`${propriété:valeur par défaut}` de MicroProfile Config, ce qui rend le module autonome : il
fonctionne même si le fichier `application.properties` est remplacé. Le tutoriel batch utilise la
forme courte `{tavern.brewery.nightly-cron}` ; les deux sont valides.

`identity` donne un nom stable à chaque tâche. Sans lui, Quarkus dérive l'identifiant de la classe
et de la méthode : renommer une méthode changerait alors l'identifiant et casserait les appels à
`pause()` / `resume()` ou les éventuelles métriques.

## 3. Une méthode annotée ne contient que l'orchestration

```java
@ApplicationScoped
public class CorveesProgrammees {

    @Inject
    RegistreDesCorvees registre;

    @Inject
    LanceurBatch lanceurBatch;

    @Scheduled(identity = IdentifiantsCorvees.PRESSION, every = "${corvees.pression.every:10m}")
    void verifierPressionFuts(ScheduledExecution execution) {
        controlerPression(execution.getScheduledFireTime(), execution.getFireTime());
    }

    // ... les quatre autres corvées

    public void executer(TacheCorvee tache) {
        Instant maintenant = Instant.now();
        switch (tache) {
            case PRESSION -> controlerPression(maintenant, maintenant);
            // ...
        }
    }

    private void controlerPression(Instant planifiee, Instant reelle) {
        registre.enregistrer(TacheCorvee.PRESSION.identifiant(), planifiee, reelle);
    }
}
```

C'est le réflexe le plus utile avec `@Scheduled` : la méthode annotée ne fait que déléguer. Quand
elle reçoit un `ScheduledExecution`, elle se contente d'en extraire les horodatages et passe la
main à une méthode métier. On peut ainsi appeler exactement le même code depuis un test, une
commande Picocli ou l'API REST, **sans attendre la prochaine échéance** et sans avoir à fabriquer un
faux `ScheduledExecution`. La méthode `executer(TacheCorvee)` du bean sert à cela ;
`POST /corvees/{tache}` l'expose.

Le `RegistreDesCorvees` garde un compteur, les deux horodatages et le nom du fil pour chaque
corvée. C'est un état en mémoire, volontairement simple : il rend le comportement observable et
testable, ce que le scheduler ne fournit pas par lui-même.

## 4. Tests : éteindre l'horloge

Le fichier `src/test/resources/application.properties` désactive le scheduler :

```properties
quarkus.scheduler.enabled=false
```

Les tests appellent donc directement `corvees.executer(TacheCorvee.XXX)` et vérifient le registre.
Aucun test ne dépend d'un `Thread.sleep()`, d'une fréquence ou de l'heure de la machine.

Un second test utilise un profil dédié pour vérifier que les cinq tâches sont bien enregistrées :

```java
public class SchedulerActifProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.scheduler.enabled", "true",
                "quarkus.scheduler.start-mode", "halted");
    }
}
```

`start-mode = halted` est le réglage à connaître : le scheduler démarre, **enregistre les tâches
et calcule leurs prochaines échéances**, mais ne déclenche rien tant que `resume()` n'est pas
appelé. On peut donc vérifier les identités, `getNextFireTime()` et le pause/resume avec zéro effet
de bord. À l'inverse, avec `enabled=false`, `getScheduledJobs()` lève une
`UnsupportedOperationException` (un test le vérifie) : c'est pour cela que `GET /corvees/taches`
commence par tester `scheduler.isStarted()` et renvoie une liste vide.

## 5. Bloquant ou non bloquant

Par défaut, une méthode `@Scheduled` s'exécute sur le pool de threads bloquants de Quarkus. C'est
le bon choix pour une corvée qui fait de l'I/O, interroge une base ou appelle un service distant.
Le champ `fil` de `EtatCorvee` le montre dans la réponse JSON.

Deux façons de basculer sur la boucle d'événements Vert.x :

- retourner un `Uni<Void>` ou un `CompletionStage<Void>` ;
- annoter la méthode avec `@NonBlocking` — c'est le cas du nettoyage du comptoir.

Ce choix ne vaut que pour les déclenchements du scheduler. Une corvée forcée par
`POST /corvees/{tache}` ou par un test s'exécute sur le fil de l'appelant : pour voir
`vert.x-eventloop-thread-…` dans le champ `fil` du comptoir, il faut attendre un vrai passage, par
exemple en dev avec `corvees.comptoir.every=5s`.

Sur la boucle d'événements, **aucun appel bloquant n'est toléré** : pas de `Thread.sleep()`, pas de
client HTTP synchrone, pas de requête JDBC. Une corvée non bloquante doit rester une petite
manipulation en mémoire — typiquement mettre à jour un compteur ou publier un événement. Quand le
doute subsiste, rester bloquant : un worker thread supplémentaire coûte moins cher qu'une boucle
d'événements figée.

## 6. Prévu vs réel, et chevauchements

Une méthode `@Scheduled` peut recevoir un `ScheduledExecution` :

```java
@Scheduled(identity = IdentifiantsCorvees.BALAYAGE, cron = "${corvees.balayage.cron:0 30 6 * * ?}")
void balayerLentree(ScheduledExecution execution) {
    balayer(execution.getScheduledFireTime(), execution.getFireTime());
}
```

- `execution.getScheduledFireTime()` : l'heure **prévue** par le cron ;
- `execution.getFireTime()` : l'heure **réelle** de déclenchement.

Comparer les deux permet de détecter un retard : redémarrage au mauvais moment, thread occupé,
JVM en pause. Le `Trigger` exposé par l'API va dans le même sens avec `getNextFireTime()` et
`isOverdue()`. La tolérance se règle avec `quarkus.scheduler.overdue-grace-period`.

Le paramètre `concurrentExecution = SKIP` évite qu'une seconde occurrence démarre pendant que la
première tourne encore. C'est utile pour le ramassage des verres : si une exécution dure plus
d'une heure, on préfère sauter un passage que d'envoyer deux commis dans la même salle. Le défaut,
`PROCEED`, laisse les deux tourner.

**Mais `SKIP` ne protège qu'une seule JVM.** Trois répliques de la taverne ont trois schedulers
indépendants, et trois valets balaient la même salle. Le verrou distribué est hors du périmètre de
`quarkus-scheduler` : voir le paragraphe 9.

## 7. Le pont vers le batch à 3 h 00

Le déclencheur est isolé derrière une interface :

```java
public interface LanceurBatch {
    void lancerBrassageNocturne();
}
```

```java
@Scheduled(identity = IdentifiantsCorvees.INTENDANT, cron = "${corvees.batch.cron:0 0 3 * * ?}")
void reveillerLIntendant() {
    lanceurBatch.lancerBrassageNocturne();
    registre.enregistrer(TacheCorvee.INTENDANT.identifiant());
}
```

Ce module fournit `LanceurBatchMemoire`, qui journalise et compte les appels : il reste autonome et
testable sans dépendance JBeret. Dans une vraie application, on injecte `JobOperator` et on
remplace le corps par :

```java
long executionId = jobOperator.start("nightly-brew-job", new Properties());
```

C'est exactement ce que fait `NightlyBrewScheduler` dans le
[module batch](../batch-processing-tutorial). Deux précautions en la matière :

1. **N'avoir qu'un seul déclencheur.** Le module batch a son propre cron à 02 h 00. Si l'on ajoute
   le réveil à 03 h 00, il faut retirer l'ancien `@Scheduled` ou l'un des deux jobs lancera le
   brassage en double.
2. **Garder le job idempotent.** Une panne pendant le démarrage peut laisser le scheduler sans
   nouvelles de l'état réel du job. Le batch reste la source de vérité via ses tables JBatch.

## 8. Observer et piloter les corvées

| Endpoint | Effet |
| :--- | :--- |
| `GET /corvees` | Compteurs, horodatages prévu/réel et fil de chaque corvée passée |
| `GET /corvees/taches` | Identité, prochaine et précédente échéance, retard (via `Scheduler.getScheduledJobs()`) |
| `POST /corvees/{tache}` | Force une corvée (`PRESSION`, `VERRES`, `BALAYAGE`, `COMPTOIR`, `INTENDANT`) |

L'API `Scheduler` permet aussi de piloter une tâche à chaud, sans redéployer :

```java
scheduler.pause(IdentifiantsCorvees.PRESSION);
boolean enPause = scheduler.isPaused(IdentifiantsCorvees.PRESSION);
scheduler.resume(IdentifiantsCorvees.PRESSION);
```

C'est pratique pour suspendre une corvée pendant une maintenance. Attention : l'état est en mémoire
et propre à l'instance. Sur un parc de plusieurs pods, il faut piloter **tous** les pods, ou passer
par un mécanisme de configuration centralisé.

## 9. Et si la taverne a trois répliques ?

Le scheduler par défaut stocke ses déclenchements en mémoire. Avec trois instances :

- chacune exécute ses propres corvées — `SKIP` ne voit pas les autres JVM ;
- un redémarrage perd les échéances : une corvée prévue pendant l'arrêt ne sera pas rattrapée ;
- `pause()` sur un pod ne concerne que ce pod.

Quarkus propose une implémentation alternative basée sur **Quartz** (`quarkus-quartz`). Avec un
*JDBC store* partagé et le mode cluster de Quartz, les instances se coordonnent par verrous en base :
une seule exécute un job à la fois. Le reste de l'API reste identique — c'est un choix de
dépendance et de configuration, pas de réécriture des méthodes `@Scheduled`.

Ce n'est pas gratuit : il faut une table de journal Quartz, gérer sa migration, et accepter la
contention sur la base. Pour des corvées légères, une approche souvent suffisante est de rendre
l'action **idempotente** et de tolérer les doublages — par exemple un comptage dans Redis ou une
contrainte unique en base. Le bon critère est simple : est-ce que deux valets qui balaient la même
salle posent un problème métier ? Si oui, il faut un verrou distribué. Sinon, l'idempotence suffit.

Quoi qu'il arrive, `@Scheduled` n'est **pas** une file de tâches : pas de persistance garantie, pas
de retry, pas de transaction, pas de back-pressure. Dès que le traitement devient long, fragile ou
transactionnel, on repasse à un batch, une file de messages ou un mécanisme avec accusé de
réception.

## 10. Tests et vérification

```bash
./mvnw -pl operations/scheduler-tutorial test
```

Quatorze tests, tous déterministes :

- `CorveesProgrammeesTest` : les cinq corvées comptées, l'écart prévu / réel conservé par le
  registre, le fil de l'appelant pour une corvée forcée, et le lancement du batch lors du réveil de
  l'intendant ;
- `CorveesResourceTest` : liste JSON, corvée forcée, 404 sur une corvée inconnue, exception de
  `getScheduledJobs()` et liste vide quand le scheduler est désactivé ;
- `SchedulerActifTest` : cinq tâches enregistrées avec une échéance, exposition via
  `GET /corvees/taches`, pause et reprise.

Le module n'a besoin ni de Docker, ni de base de données, ni d'attente : `./mvnw -pl operations -amd verify`
le construit en CI comme les autres tutoriels d'exploitation.

## Pour aller plus loin

- [La Taverne Nocturne : traiter les flux avec Quarkus Batch](../batch-processing-tutorial) —
  le pendant « données massives » de ce tutoriel.
- [Guide Quarkus — Scheduling periodic tasks](https://quarkus.io/guides/scheduler)
- [Référence Quarkus — Scheduler](https://quarkus.io/guides/scheduler-reference)
