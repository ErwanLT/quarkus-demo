# FitNesse tutorial

> *Article à venir.*

Tutoriel FitNesse : cinq pages wiki Slim montrent autant d'usages — appel REST, E2E IHM avec capture d'écran, règles métier en table de décision, scénario réutilisé avec `!define`, et contrat HTTP d'un POST. L'objectif n'est pas de remplacer les tests JUnit, mais de montrer comment un test d'acceptation lisible par un non-développeur peut piloter le même système.

## Périmètre

- endpoints `GET /taverne/accueil?nom=Bilbon`, `POST /taverne/reservation` et page statique `/taverne.html` sur le port `8126` ;
- `TaverneTests` : `GreetingFixture` traduit une ligne de table de décision en appel HTTP ;
- `TaverneUiTests` : `TaverneUiFixture` ouvre Chrome en headless, remplit le champ, clique et vérifie le message affiché ;
- `ReglesTarifTests` : `TarifFixture` calcule le prix selon le type de chambre, le nombre de nuits et le statut membre — la règle reste lisible dans la table ;
- `ScenarioTests` : un scénario `accueil pour` est appelé pour deux profils définis une fois avec `!define` (syntaxe `${BILBON}` dans les cellules de la table de décision) ;
- `ApiContratTests` : `ReservationFixture` envoie un POST JSON et vérifie les statuts `201`/`400` et le corps de réponse ;
- `InteractionAwareFixture` intercepte les exceptions Slim : en cas d'échec, la cellule rouge contient le message, un lien vers le PNG et la capture encodée en base64 ;
- exécution en CI via un test JUnit qui lance FitNesse dans une JVM séparée et vérifie les compteurs `right`/`wrong`/`exceptions` des cinq pages.

Les pages sont stockées au format `.wiki` de FitNesse : `src/test/fitnesse/FitNesseRoot/_root.wiki`, `FrontPage.wiki`, `SetUp.wiki` et les cinq pages de test. Le bouton **Test** vient de l'en-tête YAML `---\nTest\n---\n` ; il n'y a ni `content.txt`, ni `properties.xml`, ni répertoire par page. FitNesse est lancé directement sur `src/test/fitnesse` avec `-DOmittingUpdates=true` : les pages sont donc la source de vérité, et une modification faite dans l'interface est écrite dans le `.wiki` suivi par Git. Seuls les artefacts générés sont ignorés : `FitNesseRoot/files/` (historique, captures), `RerunLastFailures_*.wiki` et `RecentChanges.wiki` (page que FitNesse recrée à chaque édition via l'UI). Le classpath Selenium reste fourni au serveur Slim par `!path target/fitnesse/lib/*` (jars copiés par `maven-dependency-plugin`). Les ports `8127` (wiki) et `8130` (Slim) sont réservés à ce tutoriel. Selenium Manager découvre Chrome automatiquement : aucun `chromedriver` à installer.

## Pourquoi une JVM séparée ?

Lancé dans le classloader de `@QuarkusTest`, FitNesse entre en conflit avec les dépendances du projet : Groovy 3 de FitNesse contre Groovy 4 de RestAssured, et Velocity contre le `LogManager` de JBoss. Le test JUnit garde la main sur le cycle de vie du processus, mais FitNesse et Slim tournent dans leur propre JVM. Le serveur Slim charge les fixtures depuis les `!path` de la page : la ligne `!path target/fitnesse/lib/*` (jars copiés par `maven-dependency-plugin`) rend Selenium disponible sans dépendre du classpath interne de Surefire.

Le code de sortie de `FitNesseMain -c <page>` ne reflète pas les échecs d'assertions : le test appelle donc le responder HTTP `?responder=test&format=xml` et vérifie explicitement `wrong`, `exceptions` et `right`. Une valeur attendue volontairement fausse fait échouer le build, ce qui a été vérifié.

## Lancer le tutoriel

```bash
./mvnw -B -f integration/fitnesse-tutorial/pom.xml test
```

Depuis le reactor, avec le profil qui l'exclut de la CI par défaut :

```bash
./mvnw -B -Pfitnesse -pl integration -amd verify
```

Pour consulter le wiki dans un navigateur :

```bash
./mvnw -B -q -f integration/fitnesse-tutorial/pom.xml dependency:build-classpath -Dmdep.outputFile=target/cp.txt
cd integration/fitnesse-tutorial
java -cp "$(cat target/cp.txt)" -Dslim.port=8130 -DOmittingUpdates=true fitnesseMain.FitNesseMain -p 8127 -d src/test/fitnesse
```

Ouvrir ensuite `http://localhost:8127/FrontPage` et choisir une des cinq pages, puis cliquer sur **Test**. L'application Quarkus doit être démarrée séparément (`quarkus:dev`) pour que les fixtures puissent l'appeler. Les liens **Edit** / **Save** de FitNesse modifient directement les `.wiki` de `src/test/fitnesse/FitNesseRoot/` : `git diff` montre immédiatement les changements de scénario. Les fichiers `aFile.txt`, `bFile.txt`, `partition.txt` venaient des suites d'acceptation intégrées à FitNesse quand on cliquait sur **Test** depuis la racine : avec `-DOmittingUpdates=true` ils ne sont plus générés et ceux qui traînaient ont été supprimés.

Le second scénario de `TaverneUiTests.wiki` est volontairement en échec (`entrer Frodon` alors que l'attendu reste `Bienvenue à la taverne, Bilbon !`) : la cellule devient rouge avec le message `Attendu : ... | Obtenu : ...`, la capture est intégrée au wiki et le PNG apparaît dans `src/test/fitnesse/FitNesseRoot/files/screenshots/` (répertoire ignoré par Git). Remettre la saisie à `Bilbon` — ou corriger l'attendu en `Frodon` — rend le test vert.

Ce module fait partie du domaine `integration` mais, comme `operations/load-testing-tutorial`, il est hors du build par défaut : il faut activer le profil `-Pfitnesse` pour le lancer via `./mvnw -Pfitnesse -pl integration -amd verify`. En local, `./mvnw -B -f integration/fitnesse-tutorial/pom.xml test` suffit.
