# FitNesse tutorial

> *Article à venir.*

Tutoriel FitNesse : les règles de la taverne **The Falling Whale** sont écrites dans un wiki, en tables lisibles par le tavernier comme par le développeur, et vérifiées à chaque exécution contre l'application Quarkus réelle. L'objectif n'est pas de remplacer les tests JUnit, mais de montrer comment des spécifications exécutables, lisibles par un non-développeur, pilotent le même système — et l'étendue de ce que le framework sait faire.

## L'application testée

Une taverne Quarkus sur le port `8126` :

| Endpoint | Rôle |
|---|---|
| `GET /taverne/accueil?nom=` | message de bienvenue |
| `GET /taverne/carte?categorie=` | carte du jour, triée par catégorie puis par prix |
| `GET /taverne/chambres` | chambres, prix par nuit et lits libres |
| `GET /taverne/tarif?chambre=&nuits=&membre=` | devis détaillé (prix de base, remises, total) |
| `POST /taverne/reservations` | réservation : `201` + `Location`, `400` si la demande est invalide, `409` si la chambre est complète |
| `GET /taverne/reservations/{numero}` | consultation, `404` si inconnue |
| `DELETE /taverne/reservations` | remise à zéro du registre (utilisée par la page `SetUp`) |

Les règles métier vivent dans `fr.eletutour.fitnesse.domain` (`Tarif`, `Carte`, `Registre`). La page `/taverne.html` est un vrai écran : accueil, carte, disponibilité des chambres, devis recalculé en direct et formulaire de réservation, le tout alimenté par l'API.

## Le wiki

```
FitNesseRoot/
├── FrontPage.wiki                       accueil : quoi lire, quelle page montre quoi
├── TaverneSuite.wiki                    Suite : sommaire, tags, !define et !path hérités
└── TaverneSuite/
    ├── SuiteSetUp.wiki                  une fois par suite : la taverne est-elle ouverte ?
    ├── SetUp.wiki                       avant chaque page : import + registre remis à zéro
    ├── ScenarioLibrary.wiki             vocabulaire métier du navigateur (scénarios)
    ├── AccueilDesVoyageurs.wiki
    ├── GrilleTarifaire.wiki
    ├── CarteDuJour.wiki
    ├── ReservationAuComptoir.wiki
    ├── ParcoursEnSalle.wiki
    └── EchecVolontaireAvecCapture.wiki  tag demo-echec, rouge exprès
```

Chaque page de test commence par **la règle en français**, puis les exemples qui font foi ; le câblage technique est replié dans un encadré « Pour les développeurs ».

| Page | Sujet métier | Ce que FitNesse y démontre | Fixture |
|---|---|---|---|
| `AccueilDesVoyageurs` | saluer chaque voyageur par son nom | table de décision sur l'API REST, variables `!define`, colonne commentaire `#`, expressions régulières `=~/.../`, cellule littérale `!-...-!` | `AccueilDesVoyageurs` |
| `GrilleTarifaire` | prix d'une nuitée et remises cumulées | table de décision **sans HTTP**, branchée sur la classe métier `Tarif` ; `execute()`/`reset()` ; comparaisons `<20`, `>=1500`, intervalle `10<_<30` | `GrilleTarifaire` |
| `CarteDuJour` | ce qu'on sert ce soir | `query` (surplus / manquants), `subset query` avec argument de constructeur, `ordered query` | `CarteDuJour` |
| `ReservationAuComptoir` | contrat de l'API de réservation | table de script en phrases (« le voyageur … reserve la chambre … pour … nuits »), `ensure`/`reject`, `check`/`check not`, symboles `$premiere=`, `show`, `note`, JSON brut, statuts `201`/`200`/`404`/`400`/`409` | `ComptoirDesReservations` |
| `ParcoursEnSalle` | un client réel dans Chrome | scénarios à paramètres dans la phrase (`le voyageur _ se présente`), **table de décision qui pilote un scénario** avec sorties `devis?`/`confirmation?`, `show` d'une capture d'écran même au vert | `NavigateurDeLaTaverne` |
| `EchecVolontaireAvecCapture` | à quoi ressemble une promesse non tenue | `check` en rouge (attendu / obtenu) **et** exception jaune avec capture intégrée, tag `demo-echec` | `ComptoirDesReservations`, `NavigateurDeLaTaverne` |

Grâce à la table `import` de `SetUp`, les pages écrivent `grille tarifaire` plutôt que `fr.eletutour.fitnesse.GrilleTarifaire`. La fixture navigateur ne connaît que des gestes techniques (saisir, cliquer, lire) : c'est `ScenarioLibrary` qui les traduit en phrases métier, si bien qu'aucune page de test ne contient de sélecteur CSS. Avant de lire l'écran, elle attend que la page n'ait plus d'appel réseau en cours (`body[data-chargements="0"]`) : pas de `sleep`.

### La capture d'écran en cas d'échec

`NavigateurDeLaTaverne` implémente `InteractionAwareFixture` : tout appel Slim qui lève une exception est intercepté, une capture est prise et renvoyée sous la forme `message:<<html>>`. La cellule affiche alors le message `Attendu : … | Obtenu : …`, la capture intégrée en base64 et un lien vers le PNG écrit dans `src/test/fitnesse/FitNesseRoot/files/captures/` (servi par FitNesse, ignoré par Git).

`EchecVolontaireAvecCapture` est rouge exprès : un `check` du prix attend `30` au lieu de `32`, et la vérification IHM attend qu'on salue Bilbon alors que Frodon s'est présenté. La page explique comment la remettre au vert.

### Les fichiers `.wiki`

Les pages sont stockées au format `.wiki` de FitNesse : un fichier par page, les sous-pages dans un répertoire du même nom. Le type de page (`Test`, `Suite`), l'aide et les tags viennent de l'en-tête YAML. FitNesse est lancé directement sur `src/test/fitnesse` avec `-DOmittingUpdates=true` : les pages sont donc la source de vérité, et une modification faite dans l'interface est écrite dans le `.wiki` suivi par Git. Seuls les artefacts générés sont ignorés : `FitNesseRoot/files/` (historique, captures), `RerunLastFailures_*.wiki`, `RecentChanges.wiki` et les sauvegardes `*.zip`. Les ports `8127` (wiki) et `8130` (Slim) sont réservés à ce tutoriel. Selenium Manager découvre Chrome automatiquement : aucun `chromedriver` à installer.

## Pourquoi une JVM séparée ?

Lancé dans le classloader de `@QuarkusTest`, FitNesse entre en conflit avec les dépendances du projet : Groovy 3 de FitNesse contre Groovy 4 de RestAssured, et Velocity contre le `LogManager` de JBoss. Le test JUnit garde la main sur le cycle de vie du processus, mais FitNesse et Slim tournent dans leur propre JVM. Le serveur Slim charge les fixtures depuis les `!path` de la page `TaverneSuite`, hérités par toutes les pages filles : la ligne `!path target/fitnesse/lib/*` (jars copiés par `maven-dependency-plugin`) rend Selenium et Jackson disponibles sans dépendre du classpath interne de Surefire.

Le code de sortie de `FitNesseMain -c <page>` ne reflète pas les échecs d'assertions : le test appelle donc le responder HTTP au format XML et vérifie lui-même les compteurs.

- `TaverneSuite?suite&excludeSuiteFilter=demo-echec` : la liste exacte des pages exécutées, puis pour chacune `wrong = 0`, `exceptions = 0` et le nombre de `right` attendu (une ligne supprimée d'une table se voit aussi) ;
- `TaverneSuite.EchecVolontaireAvecCapture?test` : exactement un `wrong` et une exception, et un nouveau PNG `echec-*.png` dans le répertoire des captures.

Une valeur attendue faussée dans une page verte fait échouer le build, ce qui a été vérifié.

## Lancer le tutoriel

### Prérequis

- le JDK du projet (Java 25) ;
- **Google Chrome** installé, pour la page `ParcoursEnSalle` et la démonstration d'échec : Selenium Manager le trouve tout seul, aucun `chromedriver` à installer ;
- les ports libres : `8126` (taverne), `8127` (wiki FitNesse) et `8130` (serveur Slim).

Toutes les commandes ci-dessous se lancent **depuis la racine du dépôt**.

### Option 1 — tout vérifier en une commande

```bash
./mvnw -B -f integration/fitnesse-tutorial/pom.xml test
```

Le test JUnit démarre la taverne, lance FitNesse dans sa propre JVM, exécute la suite, vérifie les compteurs, puis arrête tout. Rien à ouvrir, rien à arrêter : c'est le mode CI. Les captures prises pendant l'exécution restent dans `integration/fitnesse-tutorial/src/test/fitnesse/FitNesseRoot/files/captures/`.

Depuis le reactor, le module n'est construit qu'avec son profil (il est exclu du build par défaut, comme `operations/load-testing-tutorial`) :

```bash
./mvnw -B -Pfitnesse -pl integration -amd verify
```

### Option 2 — explorer le wiki dans le navigateur

Trois étapes, avec **deux terminaux** qui restent ouverts : un pour la taverne, un pour FitNesse.

**1. Préparer le classpath de FitNesse** (une fois, puis après chaque modification d'une fixture) :

```bash
./mvnw -B -q -f integration/fitnesse-tutorial/pom.xml process-test-classes
```

Cette phase compile l'application et les fixtures, puis copie toutes les dépendances de test (FitNesse, Selenium, Jackson…) dans `target/fitnesse/lib/`.

**2. Démarrer la taverne** dans le premier terminal :

```bash
./mvnw -B -f integration/fitnesse-tutorial/pom.xml quarkus:dev
```

L'application écoute sur `http://localhost:8126` ; la page de la taverne est sur `http://localhost:8126/taverne.html`. En mode dev, une modification de `taverne.html` ou du code métier est prise en compte au prochain appel, sans redémarrer.

**3. Démarrer FitNesse** dans le second terminal :

```bash
cd integration/fitnesse-tutorial
```

```bash
java -cp "target/fitnesse/lib/*" -Dslim.port=8130 -DOmittingUpdates=true fitnesseMain.FitNesseMain -p 8127 -d src/test/fitnesse
```

- `-p 8127` : port du wiki ;
- `-d src/test/fitnesse` : FitNesse travaille directement sur les pages suivies par Git ;
- `-DOmittingUpdates=true` : empêche FitNesse de recopier sa documentation intégrée dans ce répertoire ;
- `-Dslim.port=8130` : port du serveur Slim qui exécute les fixtures.

La commande doit être lancée depuis `integration/fitnesse-tutorial` : les `!path` de la page `TaverneSuite` (`target/classes`, `target/test-classes`, `target/fitnesse/lib/*`) sont relatifs à ce répertoire.

**4. Ouvrir le wiki** sur `http://localhost:8127/FrontPage` :

- la page d'accueil explique ce que montre chaque page ;
- `TaverneSuite` → bouton **Suite** : exécute toutes les pages (la démonstration d'échec sera rouge, c'est voulu) ;
- une page → bouton **Test** : l'exécute seule ;
- `http://localhost:8127/TaverneSuite?suite&excludeSuiteFilter=demo-echec` : toute la suite sans la page rouge.

Les boutons **Edit** / **Save** modifient directement les `.wiki` de `src/test/fitnesse/FitNesseRoot/` : `git diff` montre aussitôt le changement de spécification. Une modification de fixture Java demande de relancer l'étape 1 ; FitNesse, lui, peut rester démarré.

**5. Tout arrêter** avec `Ctrl+C` dans chacun des deux terminaux. C'est indispensable avant de relancer l'option 1, qui a besoin des mêmes ports.

### En cas de problème

| Symptôme | Cause probable |
|---|---|
| `SuiteSetUp` en jaune, « La taverne ne répond pas » | l'application n'est pas démarrée (étape 2) |
| `Could not find class` / `ClassNotFoundException` dans les tables | l'étape 1 n'a pas été lancée, ou FitNesse n'a pas été démarré depuis `integration/fitnesse-tutorial` |
| `Address already in use` | une instance précédente tourne encore sur `8126`, `8127` ou `8130` |
| pages IHM en jaune dès l'ouverture du navigateur | Chrome n'est pas installé |
