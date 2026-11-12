# Natif - Le barde figé dans la pierre 🗿

> *« Un barde ordinaire a besoin de s'accorder. Celui-ci a bu la potion des alchimistes de GraalVM : il est figé dans la pierre, et chante dès qu'on le touche. »*

Ce module montre comment compiler une application Quarkus en **binaire natif** avec GraalVM (ou Mandrel), et comment déjouer les trois pièges de l'**hypothèse du monde fermé** (*Closed-World Assumption*).

## Concept : la statue du barde

| Élément de la métaphore | Équivalent technique |
|---|---|
| Le barde de chair qui s'accorde | L'application sur la JVM (chargement de classes, JIT) |
| La statue de granite | Le binaire natif produit par `native-image` |
| La forge des alchimistes | La compilation AOT, au build |
| Le luthier qui fabrique un instrument d'après son nom | `Class.forName(...)` + réflexion |
| Les parchemins du recueil | Les ressources lues avec `getResourceAsStream` |
| La date gravée dans le socle | Un champ `static final` initialisé au build ou au run |

## Endpoints

| Méthode | Chemin | Écueil illustré |
|---|---|---|
| `GET` | `/barde/instruments/{nom}` (`luth`, `flute`, `tambour`) | Réflexion (`Class.forName`) |
| `GET` | `/barde/repertoire` | Jackson sur un type jamais exposé par REST + ressource JSON |
| `GET` | `/barde/ballades/{titre}` | Ressources (`ballades/*.txt`) |
| `GET` | `/barde/statue` | Initialisation au build vs au run |

## Les trois parades

### 1. La réflexion : `@RegisterForReflection`

`Luthier` instancie un instrument à partir d'une chaîne. GraalVM ne peut pas deviner ces classes au build : on les déclare dans `NativeConfiguration`.

```java
@RegisterForReflection(targets = { Luth.class, Flute.class, Tambour.class, Repertoire.class })
public class NativeConfiguration {
}
```

Les types **retournés** par un endpoint REST (ici `Melodie`, `Ballade`, `Statue`) sont enregistrés automatiquement par Quarkus : inutile de les annoter.

### 2. Les ressources : `quarkus.native.resources.includes`

```properties
quarkus.native.resources.includes=ballades/**
```

### 3. L'initialisation : `--initialize-at-run-time`

Quarkus initialise les classes applicatives **au build**. Le champ `Sculpteur.DATE_GRAVEE` (`static final Instant.now()`) est donc calculé pendant la compilation et figé dans le binaire : sans parade, `/barde/statue` renvoie une `dateGravee` antérieure au `reveil`.

```properties
quarkus.native.additional-build-args=--initialize-at-run-time=fr.eletutour.tavern.barde.statue.Sculpteur
```

## Ce qu'on observe sans les parades

| Endpoint | Binaire natif sans parade |
|---|---|
| `/barde/instruments/luth` | `404` : `ClassNotFoundException` |
| `/barde/repertoire` | `404` : `ballades/repertoire.json` introuvable, puis une fois la ressource incluse, `500` : `Cannot construct instance of Repertoire … this appears to be a native image` |
| `/barde/ballades/la-baleine-qui-tombe` | `404` : parchemin introuvable |
| `/barde/statue` | `200`, mais `dateGravee` = date du build |

## Mesures (MacBook M3 Pro, GraalVM CE 25.0.2, Quarkus 3.34.6)

| Métrique | JVM | Natif |
|---|---|---|
| Démarrage (`started in`) | ~0,60 s | ~0,03 s |
| RSS au repos | ~195 Mo | ~38 Mo |
| RSS après 20 000 requêtes (`ab -c 50`) | ~305 Mo | ~79 Mo |
| Artefact | 20 Mo (`quarkus-app/`) + une JVM | 54 Mo (binaire autonome) |
| Build | ~5 s | ~48 s de `native-image` (~1 min avec les tests) |

Ordres de grandeur sur un module minimal : sur une application plus riche, le build natif s'allonge et l'écart de démarrage se creuse. Le débit d'un test court n'est pas représentatif : la JVM rattrape le natif une fois le JIT chaud.

## Lancer le module

```bash
# Mode JVM
../../mvnw quarkus:dev

# Binaire natif avec GraalVM/Mandrel installé localement
../../mvnw verify -Dnative

# Binaire natif Linux, compilé dans un conteneur (sans GraalVM local)
../../mvnw package -Dnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native-micro -t taverne/barde-natif .
```

`./mvnw verify -Dnative` construit le binaire puis rejoue `BardeResourceTest` contre lui via `BardeResourceIT` (`@QuarkusIntegrationTest`).
