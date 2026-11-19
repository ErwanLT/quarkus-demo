# Les archives de la Guilde : MongoDB avec Panache

*La métaphore : la taverne tient un dossier par aventurier. Dans une base relationnelle, il faudrait
une table pour les identités et une autre pour les hauts faits, puis une jointure pour reconstituer
le dossier. Dans MongoDB, le dossier est un seul document : la fiche et ses prouesses voyagent
ensemble, et leur forme peut varier d'un aventurier à l'autre.*

Ce module montre comment utiliser **MongoDB avec Panache** dans Quarkus :

- une entité document (`PanacheMongoEntity`) et un repository Panache ;
- des **sous-documents intégrés** (les prouesses) au lieu d'une collection séparée ;
- des requêtes sur un tableau imbriqué avec la notation à points (`prouesses.rang`) ;
- pagination, index unique, pipeline d'agrégation et gestion d'erreurs centralisée (RFC 9457) ;
- Dev Services : aucun MongoDB à installer, Docker suffit.

## Lancer le module

Docker doit tourner : Quarkus démarre automatiquement un conteneur `mongo:7` et y connecte
l'application.

```bash
./mvnw quarkus:dev -pl database-access/mongodb-tutorial
```

L'application écoute sur le port **8085**.

| Ressource | URL |
| :--- | :--- |
| Swagger UI | <http://localhost:8085/q/swagger-ui> |
| Première page (avec total) | <http://localhost:8085/archives/aventuriers> |
| Filtre par classe | <http://localhost:8085/archives/aventuriers?classe=MAGE> |
| Filtre par rang de prouesse | <http://localhost:8085/archives/aventuriers?rang=DRAGON> |
| Pagination (taille de 1 à 100) | <http://localhost:8085/archives/aventuriers?page=0&taille=2> |
| Répartition par classe | <http://localhost:8085/archives/aventuriers/statistiques> |

Quelques appels :

```bash
curl -s "localhost:8085/archives/aventuriers?rang=DRAGON"
curl -s localhost:8085/archives/aventuriers/statistiques

curl -s -X POST localhost:8085/archives/aventuriers \
  -H 'Content-Type: application/json' \
  -d '{"nom":"Perceval le naïf","classe":"PALADIN","niveau":1,"bourse":3}'

curl -s -X POST localhost:8085/archives/aventuriers/<id>/prouesses \
  -H 'Content-Type: application/json' \
  -d '{"titre":"Le moulin hanté","rang":"GOBELIN","annee":1024,"recompense":7}'
```

## 1. Le document avant les tables

Un dossier ressemble à ceci dans MongoDB :

```json
{
  "_id": { "$oid": "673f..." },
  "nom": "Mirelle l'ensorceleuse",
  "classe": "MAGE",
  "niveau": 9,
  "bourse": 300,
  "prouesses": [
    { "titre": "Le dragon de la combe", "rang": "DRAGON", "annee": 1023, "recompense": 200 }
  ]
}
```

L'entité Panache ne déclare qu'une seule collection. Les prouesses sont une liste de POJOs :
aucune annotation d'entité, aucune jointure.

```java
@MongoEntity(collection = "aventuriers")
public class DossierAventurier extends PanacheMongoEntity {
    public String nom;
    public ClasseAventurier classe;
    public int niveau;
    public int bourse;
    public List<Prouesse> prouesses = new ArrayList<>();
}
```

`Prouesse` est un simple POJO avec un constructeur sans argument : le codec POJO du driver MongoDB
le sérialise en sous-document. Panache fournit `id` (un `ObjectId`) et les opérations CRUD.

Ce choix est pertinent ici parce qu'on lit toujours un dossier **avec** ses prouesses, et qu'aucune
autre partie de l'application ne manipule une prouesse isolément. Si les prouesses devaient être
partagées, recherchées ou modifiées indépendamment, il faudrait au contraire les mettre dans leur
propre collection et référencer les dossiers.

## 2. Le repository et la requête à points

Le repository hérite de `PanacheMongoRepository` :

```java
@ApplicationScoped
public class DossierAventurierRepository implements PanacheMongoRepository<DossierAventurier> {

    public Optional<DossierAventurier> parNom(String nom) {
        return find(new Document("nom", nom)).firstResultOptional();
    }

    public List<DossierAventurier> chercher(ClasseAventurier classe, Rang rang, int page, int taille) {
        Document filtre = new Document();
        if (classe != null) {
            filtre.append("classe", classe.name());
        }
        if (rang != null) {
            filtre.append("prouesses.rang", rang.name());
        }
        return find(filtre).page(page, taille).list();
    }
}
```

Le filtre `prouesses.rang` est la notation à points de MongoDB : elle cherche dans chaque élément
du tableau sans déplier le document. Le résultat contient les dossiers complets, prouesses incluses.
Si plusieurs conditions doivent correspondre à **la même** prouesse (par exemple rang et année),
utiliser `$elemMatch` ; deux filtres à points indépendants peuvent chacun correspondre à un élément
différent du tableau.

Les enums sont stockés sous leur nom (`MAGE`, `DRAGON`) : on interroge donc avec `.name()`.

## 3. Paginer et compter

`PanacheQuery` fournit la pagination sans écrire de `skip`/`limit` :

```java
return find(filtre)
        .page(page, taille)   // page 0 = première page
        .list();
```

Le endpoint expose `?page=` et `?taille=` ; `taille` vaut 10 par défaut et est limitée à 100.
La réponse enveloppe les éléments et fournit `page`, `taille`, `total` et `nombreDePages`, calculés
avec le même filtre que la liste. Cela permet à un client d'afficher « 1–10 sur 42 » et de naviguer.

## 4. Ajouter une prouesse

Ajouter un haut fait, c'est modifier la liste du document puis le réécrire :

```java
DossierAventurier dossier = repository.findByIdOptional(id).orElseThrow(...);
dossier.prouesses.add(new Prouesse(...));
repository.update(dossier);
```

C'est lisible, mais cela réécrit tout le document. Pour une liste qui grossit beaucoup, on préférera
un `$push` atomique via `PanacheUpdate`, qui ne transporte que le nouvel élément.

## 5. Un index unique pour les noms

Deux aventuriers ne peuvent pas porter le même nom. L'index est créé au démarrage, avant le
réensemencement :

```java
repository.mongoCollection().createIndex(
        Indexes.ascending("nom"),
        new IndexOptions().unique(true));
```

Le service vérifie le doublon pour renvoyer un message clair, mais l'index reste le vrai garde-fou :
si deux requêtes passent en même temps, MongoDB refuse la seconde écriture (`code 11000`). Comme
toutes les erreurs du module, elle est traduite en `409 Conflict` par le mécanisme décrit ci-dessous.

## 6. Erreurs : un seul format, un seul point de construction

Plutôt que d'écrire un mapper qui fabrique sa propre réponse pour chaque cas, tous les mappers
héritent de `AbstractExceptionMapper`. Celui-ci construit toujours le même `Problem` (RFC 9457) :
`type`, `title`, `status`, `detail`, `instance`, `timestamp` et un objet `additional` extensible.
C'est le même principe que le tutoriel
[Gestion d'exceptions](../../core-concept/exception-handling-tutorial).

```java
Problem problem = Problem.of(status(exception), title(exception))
        .type(type(exception))
        .detail(detail(exception))
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC).toString());
if (uriInfo != null && uriInfo.getRequestUri() != null) {
    problem.instance(uriInfo.getRequestUri().toString());
}
enrich(problem, exception);
```

Chaque mapper ne fournit que ce qui le caractérise : statut, titre, éventuellement un type, un
détail ou un enrichissement. Les erreurs métier sont déclarées une fois dans un enum :

```java
NOM_DEJA_PRIS("ARCHIVES_NOM_DEJA_PRIS", 409, "Nom déjà pris",
        "Un dossier porte déjà ce nom dans les archives."),
```

`ArchivesException` porte le statut, le titre et les données contextuelles. Le service ne connaît
que des fabriques :

```java
if (repository.parNom(creation.nom()).isPresent()) {
    throw ArchivesException.nomDejaPris(creation.nom());
}
```

`BusinessExceptionMapper` en déduit le `type` (`urn:problem:archives:ARCHIVES_NOM_DEJA_PRIS`) et
remplit `additional` avec `code`, `nom`, `identifiant` ou `codeMongo` selon le cas. Un client peut
donc afficher un message lisible *et* réagir à un code stable.

Les erreurs qui ne viennent pas du métier passent par des mappers spécialisés :

| Mapper | Déclencheur | Réponse |
| :--- | :--- | :--- |
| `ConstraintViolationExceptionMapper` | `@Valid` sur les DTO et contraintes sur `?page=` / `?taille=` | `400`, `additional.errors` avec un message par violation |
| `MongoWriteExceptionMapper` | index unique (`code 11000`) | `409`, le même `type` que `NOM_DEJA_PRIS` |
| `MongoWriteExceptionMapper` | autre refus d'écriture MongoDB | `500`, `additional.codeMongo` |
| `GlobalExceptionMapper` | erreur inattendue | `500`, message technique masqué ; une `WebApplicationException` garde son statut |

Une réponse complète ressemble à ceci :

```json
{
  "type": "urn:problem:archives:ARCHIVES_DOSSIER_INTROUVABLE",
  "title": "Dossier introuvable",
  "status": 404,
  "detail": "Aucun dossier ne porte l'identifiant 000000000000000000000000.",
  "instance": "http://localhost:8085/archives/aventuriers/000000000000000000000000",
  "timestamp": "2026-11-17T12:44:31Z",
  "additional": {
    "code": "ARCHIVES_DOSSIER_INTROUVABLE",
    "identifiant": "000000000000000000000000"
  }
}
```

## 7. Agréger avec le pipeline MongoDB

Les statistiques ne sont pas calculées en Java : elles sont déléguées à MongoDB.

```java
List<Bson> pipeline = List.of(
        Aggregates.group("$classe",
                Accumulators.sum("effectif", 1),
                Accumulators.sum("bourse", "$bourse")),
        Aggregates.sort(Sorts.ascending("_id")));
```

Chaque ligne renvoyée contient la classe, l'effectif et la somme des bourses. Le repository décode
les `Document` résultats en `StatistiquesClasse`, exposé par `GET /archives/aventuriers/statistiques`.

## 8. Dev Services et tests

Aucune configuration de connexion n'est nécessaire : dès que `quarkus-mongodb-panache` est présent
et qu'aucune URL n'est fournie, Dev Services démarre un conteneur. L'image est épinglée :

```properties
quarkus.mongodb.database=archives
quarkus.mongodb.devservices.image-name=mongo:7
```

Les tests utilisent la même mécanique, avec une base dédiée :

```properties
%test.quarkus.mongodb.database=archives-test
```

```bash
./mvnw -pl database-access/mongodb-tutorial test
```

La suite couvre le repository et les endpoints :

- `DossierAventurierRepositoryTest` : requête sur le tableau imbriqué, pagination, agrégation et
  refus d'un doublon par l'index unique ;
- `ArchivesResourceTest` : filtres, lecture par `ObjectId`, création, ajout de prouesse, statistiques,
  `409` sur doublon, `404` sur identifiant inconnu, `400` sur identifiant mal formé ou donnée
  invalide, `500` masquée, et vérification systématique du `Problem` (`type`, `instance`,
  `timestamp`, `additional`).

Comme le build démarre un conteneur, Docker est nécessaire en local **et** en CI. Le premier run
télécharge l'image `mongo:7` (environ 275 Mo), les suivants la réutilisent.

## 9. Ce que MongoDB ne fait pas à votre place

- **Pas de jointure** : soit on intègre (comme les prouesses), soit on duplique, soit on fait
  plusieurs requêtes. Le choix se fait à la conception, pas au moment de la requête.
- **Pas de schéma imposé** : la souplesse est utile pour des attributs variables, mais elle reporte
  les vérifications dans le code et les tests.
- **Document limité à 16 Mo** : une liste intégrée qui grandit sans fin finira par poser problème.
- **Migrations** : Flyway et Liquibase ne parlent pas MongoDB. Il existe des outils dédiés
  (Mongock, par exemple), à traiter dans un sujet séparé.
- **Transactions** : elles exigent une topologie MongoDB compatible et une session transactionnelle ;
  l'application doit les configurer explicitement. Dev Services seul ne signifie pas que les
  opérations de ce module s'exécutent dans une transaction. Elles ne remplacent pas une modélisation
  adaptée.

Pour le versant relationnel, comparer avec [Hibernate Panache](../panache-tutorial) et
[Flyway & Liquibase](../flyway-liquibase-tutorial).

Le bourgmestre peut désormais retrouver un aventurier par son nom, ses prouesses ou sa classe sans
ouvrir trois registres à la fois.
