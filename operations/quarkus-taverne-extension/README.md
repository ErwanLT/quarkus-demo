# Extension Quarkus personnalisée - La forge aux extensions

[Article](https://www.sfeir.dev/back/quarkus-la-forge-aux-extensions/)

Une extension Quarkus complète, forgée de l'intérieur : un binding CDI `@Taverne`, un intercepteur
qui annonce chaque quête dans les logs, une configuration typée, un `BuildStep` et une carte Dev UI.
Le but est de montrer la mécanique **runtime / deployment**, pas d'utiliser une extension existante.

## Structure

```text
quarkus-taverne-extension/
├── runtime/            # quarkus-taverne : ce qui est livré à l'application
│   ├── Taverne.java            @InterceptorBinding, @Nonbinding sur `quete`
│   ├── TaverneInterceptor.java intercepte et journalise
│   └── TaverneConfig.java      @ConfigMapping(prefix = "taverne"), phase RUN_TIME
├── deployment/         # quarkus-taverne-deployment : la forge, exécutée au build
│   ├── TaverneProcessor.java   @BuildStep : FeatureBuildItem, AdditionalBeanBuildItem, Dev UI
│   └── TaverneInterceptorTest  QuarkusUnitTest (épreuve de trempe)
└── integration-tests/  # une application consommatrice : @Taverne sur un endpoint REST
```

Deux règles de nommage portent toute la mécanique :

- l'artifactId `runtime` doit être le **préfixe exact** de celui du `deployment`
  (`quarkus-taverne` / `quarkus-taverne-deployment`) : c'est ce couplage que Quarkus utilise pour
  rattacher les `BuildItem` à l'extension ;
- le module `runtime` doit produire `META-INF/quarkus-extension.yaml` (via
  `quarkus-extension-maven-plugin`) et être indexé (Jandex), sinon l'extension est un artefact muet :
  aucune carte Dev UI, aucun `BuildStep` découvert.

> Ce dossier est volontairement **détaché du POM racine** : une extension ne doit pas hériter des
> dépendances applicatives du mono-repo (REST, OpenAPI, Jackson…). Il n'a pas de `<parent>` vers
> `quarkus-demo`, seulement l'import du BOM Quarkus. Il n'est donc pas construit par
> `./mvnw verify` à la racine : voir ci-dessous.

## Construire et tester l'extension

```bash
cd operations/quarkus-taverne-extension

# Installe quarkus-taverne et quarkus-taverne-deployment dans le dépôt local,
# puis exécute le QuarkusUnitTest du module deployment.
../../mvnw clean install
```

L'installation est nécessaire : le module `integration-tests` résout `quarkus-taverne` depuis le
dépôt Maven local. Un `mvn verify` seul ne publierait pas l'artefact.

## Lancer l'application de démonstration

```bash
cd operations/quarkus-taverne-extension/integration-tests
../../../mvnw quarkus:dev
```

L'application écoute sur <http://localhost:8122>. L'endpoint de test :

```bash
curl http://localhost:8122/quete
# Le héros trouve un coffre. Il contient... un README.
```

Dans la console, l'intercepteur annonce la quête avant de rendre la main :

```text
[la-taverne] Que l'on m'entende ! La quête « Explorer le donjon de la Taverne » commence !
[la-taverne] Explorer le donjon de la Taverne : quête accomplie.
```

En mode dev, la Dev UI expose une carte **La carte de la Taverne**
(`deployment/src/main/resources/dev-ui/qwc-taverne-map.js`).

## Configuration

`runtime/src/main/java/.../TaverneConfig.java` définit deux propriétés, modifiables à chaud
(phase `RUN_TIME`) :

| Propriété | Défaut | Effet |
|---|---|---|
| `taverne.enabled` | `true` | Coupe les annonces sans retirer l'extension |
| `taverne.ton` | `BARDE` | `BARDE`, `GROGNON` ou `SOBRE` |

```properties
taverne.ton=GROGNON
# taverne.enabled=false
```

Le `@Nonbinding` sur `Taverne.quete()` est le piège classique : sans lui, CDI considère
`@Taverne(quete = "A")` et `@Taverne(quete = "B")` comme deux bindings distincts et l'intercepteur
ne se déclenche plus.

## Ce que vérifie le test

`deployment/.../TaverneInterceptorTest` boote une application Quarkus à blanc avec `QuarkusUnitTest`
et capture le logger `la-taverne`. Il vérifie deux choses distinctes :

1. la quête renvoie son résultat intact (`context.proceed()` n'est pas dénaturé) ;
2. l'intercepteur **a réellement parlé**, en citant le nom de la quête.

Le second point est celui qui compte : un intercepteur jamais déclenché laisserait le premier test
au vert, puisque `proceed()` renvoie de toute façon le résultat d'origine.
