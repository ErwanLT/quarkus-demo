# Valider les réservations de la taverne

*Article à venir.*

Ce tutoriel montre comment valider les données entrantes d'une application Quarkus avec **Hibernate Validator**, et
comment renvoyer les erreurs dans le même format `application/problem+json` que le tutoriel
[gestion des exceptions](../exception-handling-tutorial/README.md). Le décor reste celui de **The Falling Whale** : le
tavernier tient le registre des réservations de chambres.

Le module tourne sur le port **8125** :

```bash
./mvnw -pl core-concept/validation-tutorial quarkus:dev
# ou, depuis le dossier du module :
../../mvnw quarkus:dev
```

## Ce que la taverne accepte

`POST /validation/reservations` reçoit une réservation :

```json
{
  "aventurier": "Bilbon",
  "courriel": "bilbon@combe.fr",
  "chambre": "SUITE",
  "arrivee": "2026-12-24",
  "depart": "2026-12-26",
  "aventuriers": 2
}
```

Les chambres connues sont listées par `GET /validation/chambres` : `DORTOIR`, `SUITE`, `PLACARD`.

## 1. Les annotations standard

`ReservationRequest` est un `record` annoté avec les contraintes fournies par Bean Validation :

```java
@NotBlank(message = "Le nom de l'aventurier est requis")
@Size(min = 2, max = 40, message = "Le nom doit comporter entre 2 et 40 caractères")
String aventurier,

@NotBlank(message = "Le courriel est requis")
@Email(message = "Le courriel doit être une adresse valide")
String courriel,

@Min(value = 1, message = "Au moins un aventurier est nécessaire")
@Max(value = 4, message = "Une chambre accueille au plus 4 aventuriers")
int aventuriers
```

`@FutureOrPresent` et `@Future` vérifient les dates. `@NotBlank` sur `courriel` est important : `@Email` considère qu'une
chaîne vide est valide, donc sans lui un courriel absent ou vide passe la validation. La ressource porte `@Valid` sur le
paramètre : Quarkus déclenche la validation avant d'appeler le service, et le DTO n'atteint jamais la logique métier
s'il est invalide.

## 2. Une enum pour les valeurs fermées

Les chambres sont un ensemble fermé : trois valeurs, connues à la compilation. Écrire une annotation custom pour
vérifier une `String` serait un détour — le **type** du champ suffit :

```java
@NotNull(message = "Le type de chambre est requis")
TypeChambre chambre,
```

```java
public enum TypeChambre {
    DORTOIR, SUITE, PLACARD;

    @JsonCreator
    public static TypeChambre depuis(String valeur) {
        // ... ouElseThrow(new ValeurChambreInconnueException("chambre", valeur))
    }
}
```

Une valeur comme `"AUBERGE"` est refusée pendant la désérialisation JSON, avant même la validation Bean Validation.
`@NotNull` garde la main sur le cas `chambre` absent. La contrainte custom est réservée aux règles que le type ne peut
pas exprimer.

## 3. Une annotation custom de classe

Une règle porte souvent sur plusieurs champs : ici, un séjour doit durer entre 1 et 7 nuits. Aucune annotation de
champ ne peut comparer `arrivee` et `depart`. `@SejourCoherent` s'applique donc au `record` entier :

```java
@SejourCoherent
public record ReservationRequest(...) {}
```

Son validateur calcule l'écart avec `ChronoUnit.DAYS.between(...)`. Quand la règle est violée, il utilise
`buildConstraintViolationWithTemplate(...).addPropertyNode("depart")` pour rattacher l'erreur au champ `depart` :
le client reçoit un nom de champ exploitable, pas seulement le nom du bean.

## 4. La règle métier : chevauchement et atomicité

Bean Validation vérifie la forme d'une requête ; elle ne sait pas si la chambre est libre. C'est le rôle du service,
après validation. Deux points faciles à manquer :

**Le chevauchement des périodes.** Comparer seulement la date d'arrivée laisse passer une réservation qui commence
pendant un séjour déjà enregistré. Le repository teste donc les deux sens :

```java
arrivee.isBefore(existante.depart()) && existante.arrivee().isBefore(depart)
```

Les intervalles sont semi-ouverts : un nouveau séjour qui commence le matin du départ du précédent est accepté.

**L'atomicité du contrôle et de l'enregistrement.** Un `exists()` suivi d'un `save()` n'est pas atomique : deux requêtes
simultanées peuvent toutes deux passer le contrôle. La méthode `reserver` est `synchronized` et le repository utilise
un `ConcurrentHashMap`. Cette garantie reste **locale à une JVM** ; en cluster, il faudrait une contrainte d'unicité en
base ou un verrou distribué.

## 5. Le pont avec la gestion d'erreurs

Le module reprend le modèle du tutoriel exception-handling, dans son propre package pour rester autonome :

- `Problem` : un seul format JSON pour toutes les erreurs ;
- `AbstractExceptionMapper` : construction centralisée de la réponse (`type`, `title`, `status`, `detail`,
  `instance`, `timestamp`) ;
- `ConstraintViolationExceptionMapper` : `400` et un tableau `additional.errors` avec le **champ**, le **message**
  et le **nom de l'annotation violée** ;
- `DeserializationExceptionMapper` : RESTEasy enveloppe l'erreur de l'enum dans un `WebApplicationException` 400 — un
  mapper déclaré sur la cause ne serait jamais appelé. Le mapper est donc déclaré sur `WebApplicationException`, mais ne
  spécialise que les erreurs dont la cause est une `ValeurChambreInconnueException` (`field: "chambre"`, code
  `TypeChambreInconnu`). Pour toute autre erreur JAX-RS, il produit exactement la réponse de `GlobalExceptionMapper` :
  aucun comportement n'est détourné ;
- `BusinessExceptionMapper` : `409` et un `type` `urn:problem:tavern:CHAMBRE_DEJA_RESERVEE` quand la période demandée
  chevauche une réservation existante — la validation est passée, mais la règle métier refuse ;
- `GlobalExceptionMapper` : `500` sans trace de pile pour les erreurs imprévues.

Une requête invalide produit par exemple :

```json
{
  "type": "about:blank",
  "title": "Erreur de validation des données",
  "status": 400,
  "detail": "5 règle(s) de validation violée(s).",
  "instance": "http://localhost:8125/validation/reservations",
  "timestamp": "2026-11-26T12:00:00Z",
  "additional": {
    "violations": 5,
    "errors": [
      { "field": "arrivee", "message": "L'arrivée doit être aujourd'hui ou plus tard", "code": "FutureOrPresent" },
      { "field": "aventurier", "message": "Le nom de l'aventurier est requis", "code": "NotBlank" },
      { "field": "courriel", "message": "Le courriel doit être une adresse valide", "code": "Email" }
    ]
  }
}
```

Les violations sont triées par champ pour que la réponse soit stable et facile à tester.

Une chambre inconnue passe par le mapper de désérialisation :

```json
{
  "title": "Erreur de validation des données",
  "status": 400,
  "detail": "Type de chambre inconnu : AUBERGE (DORTOIR, SUITE ou PLACARD attendu)",
  "additional": {
    "errors": [
      { "field": "chambre", "message": "Type de chambre inconnu : AUBERGE (DORTOIR, SUITE ou PLACARD attendu)", "code": "TypeChambreInconnu" }
    ],
    "violations": 1
  }
}
```

## Endpoints

| Méthode | Chemin | Résultat |
|---|---|---|
| `GET` | `/validation/chambres` | les trois types de chambres du registre |
| `GET` | `/validation/reservations` | les réservations enregistrées |
| `POST` | `/validation/reservations` | `201` et la réservation créée, `400` si validation échoue, `409` si la période chevauche une réservation existante |
| `GET` | `/validation/erreur` | `500` via le mapper global |

## Tests

```bash
./mvnw -pl core-concept/validation-tutorial test
```

Les 11 tests de `ReservationResourceTest` couvrent les annotations standard (dont le courriel vide), le refus d'une
valeur d'enum inconnue, la contrainte custom de classe, le format `problem+json`, le chevauchement de séjours (avec le
cas contigu accepté), huit réservations concurrentes dont une seule réussit, le périmètre du mapper de désérialisation
et le mapper global. Les dates sont calculées par rapport à `LocalDate.now()` : la suite reste valide quel que soit le
jour de l'exécution.
