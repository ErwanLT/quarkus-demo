# Le registre des travaux : migrations de schéma avec Flyway et Liquibase

*La métaphore : avant de percer un mur porteur dans la taverne, on consigne le chantier dans le registre des travaux. Chaque intervention est numérotée, datée, et une fois signée on n'y touche plus.*

Ce module compare **Flyway** et **Liquibase** sur le même besoin : faire évoluer le schéma du Grand Livre des Comptes de la taverne sans jamais casser une base déjà en production.

Chaque outil travaille sur **sa propre base H2 en mémoire** et sur ses propres entités Panache. On peut ainsi comparer les deux historiques côte à côte, sans qu'un outil ne migre le schéma de l'autre.

```bash
./mvnw quarkus:dev -pl database-access/flyway-liquibase-tutorial
```

L'application écoute sur le port **8084**.

| Ressource | URL |
| :--- | :--- |
| Swagger UI | http://localhost:8084/q/swagger-ui |
| Registre Flyway | http://localhost:8084/taverne/registre/flyway |
| Registre Liquibase | http://localhost:8084/taverne/registre/liquibase |
| Grand Livre Flyway | http://localhost:8084/taverne/flyway/grand-livre |
| Grand Livre Liquibase | http://localhost:8084/taverne/liquibase/grand-livre |

---

## 1. Pourquoi pas `drop-and-create` ?

Le tutoriel Panache utilise `quarkus.hibernate-orm.schema-management.strategy=drop-and-create` : pratique pour une démo, **dangereux en production**. Le premier redémarrage supprime les tables et les données.

Une migration de schéma est un script versionné, joué une seule fois, puis conservé dans un historique :

* **Flyway** tient l'historique dans `flyway_schema_history` et identifie chaque script par une version (`V1`, `V2`, `V3`...).
* **Liquibase** tient l'historique dans `DATABASECHANGELOG` et identifie chaque évolution par un *changeSet* (`id` + `author`).

Dans les deux cas, on remplace `drop-and-create` par `validate` : Hibernate ne modifie plus rien, il vérifie simplement que les entités correspondent au schéma migré.

```properties
quarkus.hibernate-orm.schema-management.strategy=validate
quarkus.hibernate-orm.liquibase.schema-management.strategy=validate
```

Si une colonne manque ou si un type diverge, l'application refuse de démarrer. Mieux vaut une porte fermée qu'une écriture dans une colonne qui n'existe pas.

---

## 2. Deux bases, deux outils

```properties
# --- Flyway : datasource par defaut ------------------------------------------
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:taverne_flyway
quarkus.hibernate-orm.packages=fr.eletutour.database.migrations.flyway

quarkus.flyway.locations=db/migration/flyway
quarkus.flyway.migrate-at-start=true
quarkus.flyway.validate-on-migrate=true
quarkus.flyway.baseline-on-migrate=true
quarkus.flyway.baseline-version=1

# --- Liquibase : datasource nommee "liquibase" --------------------------------
quarkus.datasource.liquibase.db-kind=h2
quarkus.datasource.liquibase.jdbc.url=jdbc:h2:mem:taverne_liquibase
quarkus.hibernate-orm.liquibase.datasource=liquibase
quarkus.hibernate-orm.liquibase.packages=fr.eletutour.database.migrations.liquibase

quarkus.liquibase.liquibase.change-log=db/changelog/liquibase/db.changelog-master.xml
quarkus.liquibase.liquibase.migrate-at-start=true
quarkus.liquibase.liquibase.validate-on-migrate=true
```

> **Lecture de `quarkus.liquibase.liquibase.change-log`** : le premier `liquibase` est le **nom de la datasource**, le second est la propriété. Pour la datasource par défaut, on écrirait simplement `quarkus.liquibase.change-log=...`.

Côté Panache, chaque entité est rattachée à son unité de persistance :

```java
@Entity
@Table(name = "ligne_de_compte")
public class LigneDeCompteFlyway extends PanacheEntity { ... }

@Entity
@Table(name = "ligne_de_compte")
@PersistenceUnit(unitName = "liquibase")
public class LigneDeCompteLiquibase extends PanacheEntity { ... }
```

---

## 3. La même évolution, racontée deux fois

### Flyway : des scripts SQL numérotés

```sql
-- V1__creation_du_grand_livre.sql
CREATE SEQUENCE ligne_de_compte_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE ligne_de_compte (
    id BIGINT NOT NULL PRIMARY KEY,
    aventurier VARCHAR(255) NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    montant_pieces_or INTEGER NOT NULL,
    enregistre_le TIMESTAMP WITH TIME ZONE NOT NULL
);
```

```sql
-- V2__ajout_de_la_devise.sql
ALTER TABLE ligne_de_compte ADD COLUMN devise VARCHAR(8);
UPDATE ligne_de_compte SET devise = 'PIECE_OR' WHERE devise IS NULL;
ALTER TABLE ligne_de_compte ALTER COLUMN devise SET NOT NULL;
```

L'ordre est porté par le nom du fichier : `V1`, `V2`, `V3`... Flyway calcule aussi un **checksum**. Si un script déjà appliqué change, le démarrage échoue.

### Liquibase : des changeSets dans un changelog

```xml
<!-- db.changelog-master.xml -->
<include file="db/changelog/liquibase/db.changelog-1.0.xml"/>
<include file="db/changelog/liquibase/db.changelog-2.0.xml"/>
```

```xml
<!-- db.changelog-1.0.xml -->
<changeSet id="1-creation-du-grand-livre" author="taverne">
    <createSequence sequenceName="ligne_de_compte_SEQ" startValue="1" incrementBy="50"/>
    <createTable tableName="ligne_de_compte">
        <column name="id" type="BIGINT">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="aventurier" type="VARCHAR(255)">
            <constraints nullable="false"/>
        </column>
        <column name="libelle" type="VARCHAR(255)">
            <constraints nullable="false"/>
        </column>
        <column name="montant_pieces_or" type="INTEGER">
            <constraints nullable="false"/>
        </column>
        <column name="enregistre_le" type="TIMESTAMP WITH TIME ZONE">
            <constraints nullable="false"/>
        </column>
    </createTable>
</changeSet>
```

```xml
<!-- db.changelog-2.0.xml -->
<changeSet id="2-ajout-de-la-devise" author="taverne">
    <addColumn tableName="ligne_de_compte">
        <column name="devise" type="VARCHAR(8)"/>
    </addColumn>
    <update tableName="ligne_de_compte">
        <column name="devise" value="PIECE_OR"/>
        <where>devise IS NULL</where>
    </update>
    <addNotNullConstraint tableName="ligne_de_compte" columnName="devise" columnDataType="VARCHAR(8)"/>
</changeSet>
```

L'identité d'un changeSet est le couple `id` + `author`. On ne réutilise jamais un identifiant déjà appliqué : on ajoute un nouveau changeSet.

### Les autres formats de changelog

XML n'est pas le seul format accepté par Liquibase. Le changeSet `2.0` ci-dessus peut s'écrire de façon équivalente en **YAML**, **JSON** ou **SQL formaté**. Seul le XML est actif dans ce module ; les fichiers complets sont fournis dans [`exemples/yaml`](src/main/resources/db/changelog/liquibase/exemples/yaml), [`exemples/json`](src/main/resources/db/changelog/liquibase/exemples/json) et [`exemples/sql`](src/main/resources/db/changelog/liquibase/exemples/sql).

#### YAML

Fichier : [`db.changelog-2.0.yaml`](src/main/resources/db/changelog/liquibase/exemples/yaml/db.changelog-2.0.yaml)

```yaml
# db.changelog-2.0.yaml
databaseChangeLog:
  - changeSet:
      id: 2-ajout-de-la-devise
      author: taverne
      changes:
        - addColumn:
            tableName: ligne_de_compte
            columns:
              - column:
                  name: devise
                  type: VARCHAR(8)
        - update:
            tableName: ligne_de_compte
            columns:
              - column:
                  name: devise
                  value: PIECE_OR
            where: devise IS NULL
        - addNotNullConstraint:
            tableName: ligne_de_compte
            columnName: devise
            columnDataType: VARCHAR(8)
```

Fichier maître YAML : [`db.changelog-master.yaml`](src/main/resources/db/changelog/liquibase/exemples/yaml/db.changelog-master.yaml)

```yaml
# db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: db/changelog/liquibase/exemples/yaml/db.changelog-1.0.yaml
  - include:
      file: db/changelog/liquibase/exemples/yaml/db.changelog-2.0.yaml
```

#### JSON

Fichier : [`db.changelog-2.0.json`](src/main/resources/db/changelog/liquibase/exemples/json/db.changelog-2.0.json)

```json
{
  "databaseChangeLog": [
    {
      "changeSet": {
        "id": "2-ajout-de-la-devise",
        "author": "taverne",
        "changes": [
          {
            "addColumn": {
              "tableName": "ligne_de_compte",
              "columns": [
                {
                  "column": {
                    "name": "devise",
                    "type": "VARCHAR(8)"
                  }
                }
              ]
            }
          },
          {
            "update": {
              "tableName": "ligne_de_compte",
              "columns": [
                {
                  "column": {
                    "name": "devise",
                    "value": "PIECE_OR"
                  }
                }
              ],
              "where": "devise IS NULL"
            }
          },
          {
            "addNotNullConstraint": {
              "tableName": "ligne_de_compte",
              "columnName": "devise",
              "columnDataType": "VARCHAR(8)"
            }
          }
        ]
      }
    }
  ]
}
```

Fichier maître JSON : [`db.changelog-master.json`](src/main/resources/db/changelog/liquibase/exemples/json/db.changelog-master.json)

```json
{
  "databaseChangeLog": [
    {
      "include": {
        "file": "db/changelog/liquibase/exemples/json/db.changelog-1.0.json"
      }
    },
    {
      "include": {
        "file": "db/changelog/liquibase/exemples/json/db.changelog-2.0.json"
      }
    }
  ]
}
```

#### SQL formaté

Fichier : [`db.changelog.sql`](src/main/resources/db/changelog/liquibase/exemples/sql/db.changelog.sql)

Un changelog SQL doit commencer par `--liquibase formatted sql` et chaque évolution doit porter un commentaire `--changeset auteur:id`.

```sql
--liquibase formatted sql

--changeset taverne:2-ajout-de-la-devise
ALTER TABLE ligne_de_compte ADD COLUMN devise VARCHAR(8);
UPDATE ligne_de_compte SET devise = 'PIECE_OR' WHERE devise IS NULL;
ALTER TABLE ligne_de_compte ALTER COLUMN devise SET NOT NULL;
```

Un même fichier SQL peut contenir plusieurs blocs `--changeset`. Pour répartir les évolutions dans plusieurs fichiers SQL, gardez un fichier maître XML, YAML ou JSON qui les référence avec `include` : c'est la méthode portable selon les versions de Liquibase.

#### Changer de format

Il faut convertir tous les fichiers puis pointer la configuration vers le nouveau fichier maître :

```properties
# XML (choix actuel du module)
quarkus.liquibase.liquibase.change-log=db/changelog/liquibase/db.changelog-master.xml

# YAML
# quarkus.liquibase.liquibase.change-log=db/changelog/liquibase/exemples/yaml/db.changelog-master.yaml

# JSON
# quarkus.liquibase.liquibase.change-log=db/changelog/liquibase/exemples/json/db.changelog-master.json

# SQL
# quarkus.liquibase.liquibase.change-log=db/changelog/liquibase/exemples/sql/db.changelog.sql
```

> Ne mélangez pas les formats dans une même chaîne de changelogs sans raison : les `include` doivent pointer vers des fichiers du format attendu, et les chemins doivent correspondre à l'extension réelle.

### Le triptyque expand / backfill / contract

Les deux `V2` / changeSets `2.0` suivent la même méthode :

1. **Expand** : ajouter la colonne `devise` en nullable. Aucune ligne existante n'est bloquée.
2. **Backfill** : remplir les lignes existantes avec `PIECE_OR`.
3. **Contract** : passer la colonne en `NOT NULL`.

Une colonne `NOT NULL` sans valeur par défaut dès le premier `ALTER` échoue sur la moindre ligne déjà présente en production.

> **Le piège de la V1** : `PanacheEntity` ne génère pas un `IDENTITY` mais un id issu d'une séquence nommée `ligne_de_compte_SEQ` (incrément 50 par défaut). Un `V1` écrit à la main avec `GENERATED BY DEFAULT AS IDENTITY` et sans la séquence passe la migration, puis casse Hibernate au démarrage avec `Schema validation: missing sequence`. Le script doit décrire exactement ce qu'Hibernate attend.

---

## 4. Adopter une base existante

Une base de production créée par l'ancien `drop-and-create` possède déjà les tables. Il ne faut surtout pas rejouer la création.

### Flyway : baseline

```properties
quarkus.flyway.baseline-on-migrate=true
quarkus.flyway.baseline-version=1
```

Flyway constate un schéma non vide, inscrit la version 1 comme **baseline** sans exécuter `V1`, puis applique `V2`. Une base neuve, elle, joue réellement `V1` puis `V2`.

### Liquibase : changelogSync

Liquibase ne baseline pas automatiquement. Pour adopter une base existante, on marque les changeSets déjà représentés comme exécutés avec l'outil CLI Liquibase :

```bash
liquibase \
  --url="jdbc:h2:file:./data/taverne" \
  --changeLogFile=db/changelog/liquibase/db.changelog-master.xml \
  changelogSync
```

On exécute cette étape une seule fois sur la base existante, puis on laisse `migrate-at-start` appliquer les changeSets suivants. Pour une base neuve, aucun `changelogSync` n'est nécessaire.

---

## 5. Lire le registre

```bash
curl http://localhost:8084/taverne/registre/flyway
```

```json
{
  "outil": "flyway",
  "versionCourante": "2",
  "travauxAppliques": [
    { "version": "1", "description": "creation du grand livre", "auteur": null, "etat": "SUCCESS" },
    { "version": "2", "description": "ajout de la devise", "auteur": null, "etat": "SUCCESS" }
  ],
  "travauxEnAttente": []
}
```

```bash
curl http://localhost:8084/taverne/registre/liquibase
```

```json
{
  "outil": "liquibase",
  "versionCourante": "2-ajout-de-la-devise",
  "travauxAppliques": [
    { "version": "1-creation-du-grand-livre", "description": "createSequence, createTable", "auteur": "taverne", "etat": "SUCCESS" },
    { "version": "2-ajout-de-la-devise", "description": "addColumn, update, addNotNullConstraint", "auteur": "taverne", "etat": "SUCCESS" }
  ],
  "travauxEnAttente": []
}
```

Les deux endpoints sont implémentés dans [`RegistreDesTravauxResource`](src/main/java/fr/eletutour/database/migrations/resource/RegistreDesTravauxResource.java). Côté Flyway, on interroge directement l'API `Flyway`; côté Liquibase, on lit les statuts des changeSets via `LiquibaseFactory`.

---

## 6. Vérifier que les entités Panache suivent le schéma

```bash
# Flyway
curl -X POST "http://localhost:8084/taverne/flyway/grand-livre/ecritures?aventurier=Grimgor&montant=30"
curl http://localhost:8084/taverne/flyway/grand-livre

# Liquibase
curl -X POST "http://localhost:8084/taverne/liquibase/grand-livre/ecritures?aventurier=Grimgor&montant=30"
curl http://localhost:8084/taverne/liquibase/grand-livre
```

Chaque écriture contient la `devise` ajoutée par la deuxième migration :

```json
[
  { "id": 1, "aventurier": "Grimgor", "libelle": "Tournee de biere", "montantPiecesOr": 30, "devise": "PIECE_OR" },
  { "id": 2, "aventurier": "Grimgor", "libelle": "Taxe de la guilde", "montantPiecesOr": 3, "devise": "PIECE_OR" }
]
```

Si la migration `V2` ou le changeSet `2.0` disparaissait, Hibernate refuserait de démarrer : l'entité déclare `devise` en `nullable = false`, le schéma ne la fournirait plus.

---

## 7. Flyway ou Liquibase ?

| Critère | Flyway | Liquibase |
| :--- | :--- | :--- |
| Format | SQL (ou Java) | XML, YAML, JSON, SQL |
| Identité d'une évolution | Version `V1`, `V2`... | `id` + `author` d'un changeSet |
| Historique | `flyway_schema_history` | `DATABASECHANGELOG` |
| Baseline d'une base existante | `baseline-on-migrate` | `changelogSync` |
| Rollback | Pas de rollback automatique en Community | ChangeSets de rollback possibles |
| Lecture | Très proche du SQL | Plus abstrait, multi-base |

**Flyway** convient bien à une équipe qui veut des scripts SQL explicites et un versionnement linéaire. **Liquibase** apporte un changelog structuré, des rollbacks et une abstraction multi-SGBD. Les deux s'intègrent de la même façon dans Quarkus et Panache : scripts versionnés, `migrate-at-start`, puis `validate` côté Hibernate.

---

## 8. Les règles d'or

1. **Un script appliqué est immuable.** Flyway vérifie son checksum, Liquibase son `id` + `author`. Pour corriger, on ajoute `V3` ou un nouveau changeSet, on ne réécrit pas l'ancien.
2. **Pas de `clean` en production.** Flyway `clean` et Liquibase `drop-all` vident le schéma. En démo locale, on repart d'une H2 mémoire neuve.
3. **Tester les migrations sur une copie de prod.** Une migration qui passe sur une base vide peut échouer sur des données réelles, surtout lors d'un `NOT NULL` ou d'un changement de type.
4. **Déployer le schéma avant le code.** Une colonne ajoutée doit exister avant que la nouvelle version de l'application ne l'utilise. Pour un `NOT NULL`, prévoir deux déploiements : expand/backfill, puis contract.

---

## Structure du module

```
src/main/java/fr/eletutour/database/migrations/
├── comptes/       DTO commun Ecriture
├── flyway/        Entité Panache et service du Grand Livre cote Flyway
├── liquibase/     Entité Panache et service du Grand Livre cote Liquibase
├── registre/      Lecture des historiques Flyway et Liquibase
└── resource/      Endpoints REST du registre et des deux grands livres

src/main/resources/
├── db/migration/flyway/                  V1 et V2 Flyway
└── db/changelog/liquibase/
    ├── db.changelog-master.xml           Changelog maitre actif
    ├── db.changelog-1.0.xml              ChangeSet 1.0 actif
    ├── db.changelog-2.0.xml              ChangeSet 2.0 actif
    └── exemples/                         Equivalents YAML, JSON et SQL non actifs
        ├── yaml/
        ├── json/
        └── sql/
```

## Tests

```bash
./mvnw test -pl database-access/flyway-liquibase-tutorial
```

4 tests vérifient que les deux migrations Flyway et les deux changeSets Liquibase sont appliqués, et que les entités Panache écrivent bien la colonne `devise` ajoutée par la seconde évolution.
