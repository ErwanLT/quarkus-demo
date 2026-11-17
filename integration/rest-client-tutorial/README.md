# Le coursier de la taverne : consommer une API avec le REST Client

*La métaphore : la Taverne ne produit pas tout ce qu'elle sert. Chaque matin, un coursier part
récupérer les contrats affichés par la Guilde des aventuriers et les recettes consignées dans le
Grimoire. Plutôt que de lui faire écrire des requêtes HTTP à la main, on lui remet un **mandat** :
une interface Java décrit les gestes autorisés, Quarkus se charge du transport.*

Ce module montre comment consommer une API REST avec Quarkus :

- un client **déclaratif** (MicroProfile REST Client) injecté comme n'importe quel bean ;
- un client **programmatique** (`QuarkusRestClientBuilder`) quand l'adresse n'est connue qu'à l'exécution ;
- la configuration par `configKey`, les en-têtes (constants, configurés ou calculés) et les timeouts ;
- la traduction des erreurs HTTP et des pannes réseau en `problem+json`, sur le modèle du tutoriel de gestion d'exceptions.

Pour rester autonome, la Guilde et le Grimoire sont **simulés dans le module** (deux ressources JAX-RS
sur le même port). Le coursier leur parle malgré tout en HTTP : c'est le vrai client Quarkus qui est
exercé, pas un appel de méthode direct.

## Lancer le module

```bash
./mvnw quarkus:dev -pl integration/rest-client-tutorial
```

L'application écoute sur le port **8088**.

| Ressource | URL |
| :--- | :--- |
| Swagger UI | <http://localhost:8088/q/swagger-ui> |
| Contrats de la Guilde | <http://localhost:8088/coursier/contrats> |
| Contrats mortels | <http://localhost:8088/coursier/contrats?difficulte=MORTEL> |
| Un contrat | <http://localhost:8088/coursier/contrats/1> |
| Signature d'un contrat | `POST http://localhost:8088/coursier/contrats/1/acceptation?coursier=Brune` |
| Recette du Grimoire | <http://localhost:8088/coursier/grimoire/hydromel> |

Quelques appels :

```bash
curl -s localhost:8088/coursier/contrats
curl -s "localhost:8088/coursier/contrats?difficulte=MORTEL"
curl -s -X POST "localhost:8088/coursier/contrats/1/acceptation?coursier=Brune"
curl -s localhost:8088/coursier/grimoire/hydromel
curl -s localhost:8088/coursier/contrats/13          # la Guilde plante : 502
curl -s "localhost:8088/coursier/contrats?difficulte=EPIQUE"   # 400
```

## 1. Le mandat : un client déclaratif

`GuildeClient` ne contient aucune implémentation. Il décrit les chemins, les paramètres et les
en-têtes ; Quarkus génère le code HTTP au build.

```java
@Path("/guilde/contrats")
@RegisterRestClient(configKey = "guilde")
@ClientHeaderParam(name = "X-Taverne", value = "${taverne.nom}")
@ClientHeaderParam(name = "X-Request-Id", value = "{numeroDeCourse}")
public interface GuildeClient {

    @GET
    List<Contrat> contrats(@QueryParam("difficulte") Difficulte difficulte);

    @GET
    @Path("/{id}")
    Contrat contrat(@PathParam("id") long id);

    @POST
    @Path("/{id}/acceptation")
    ContratAccepte accepter(@PathParam("id") long id, @HeaderParam("X-Coursier") String coursier);

    default String numeroDeCourse() {
        return UUID.randomUUID().toString();
    }
}
```

Le client s'injecte ensuite avec le qualifier `@RestClient` :

```java
@Inject
@RestClient
GuildeClient guilde;
```

Trois points à retenir :

- `@RegisterRestClient` enregistre l'interface comme bean CDI ;
- `configKey = "guilde"` découple le nom du bean du préfixe de configuration ;
- les en-têtes se posent de trois façons :

| Besoin | Outil | Exemple |
| :--- | :--- | :--- |
| Même valeur pour tous les appels, lue en configuration | `@ClientHeaderParam(value = "${propriete}")` | `X-Taverne` ← `taverne.nom` |
| Valeur recalculée à chaque appel | `@ClientHeaderParam(value = "{methode}")` | `X-Request-Id` ← `numeroDeCourse()` |
| Valeur fournie par l'appelant | `@HeaderParam` sur le paramètre | `X-Coursier` |

La Guilde simulée refuse les appels sans `X-Taverne` : si `GuildeClientTest` passe, c'est que le
client ajoute bien l'en-tête. Elle renvoie aussi le `X-Request-Id` reçu comme **bordereau** de
l'acceptation : deux signatures, deux bordereaux différents.

## 2. Configurer l'adresse et les délais

Le `configKey` pointe sur le préfixe `quarkus.rest-client.guilde` :

```properties
quarkus.rest-client.guilde.url=${GUILDE_URL:http://localhost:8088}
quarkus.rest-client.guilde.connect-timeout=1000
quarkus.rest-client.guilde.read-timeout=2000
```

- `url` est la seule propriété obligatoire ; on peut la surcharger par variable d'environnement.
- `connect-timeout` et `read-timeout` sont exprimés en **millisecondes**.
- En test, les clients pointent vers le port de test (8081), pas vers le port de dev :

```properties
%test.quarkus.rest-client.guilde.url=http://localhost:8081
```

En dev, on peut voir les requêtes sortantes dans les logs :

```properties
%dev.quarkus.rest-client.logging.scope=request-response
%dev.quarkus.rest-client.logging.body-limit=2048
```

## 3. Traduire les erreurs HTTP en erreurs métier

La gestion d'erreurs suit le modèle du tutoriel
[Gestion d'exceptions](../../core-concept/exception-handling-tutorial) : une exception métier qui
connaît son statut, un registre d'erreurs, et des `ExceptionMapper` qui produisent du
`application/problem+json` (RFC 9457). Le REST Client ajoute une étape **avant** : traduire la
réponse du service distant.

### Le registre des mésaventures

```java
public enum CoursierError {
    CONTRAT_INTROUVABLE(404, "Contrat introuvable", "..."),
    RECETTE_INTROUVABLE(404, "Recette introuvable", "..."),
    DIFFICULTE_INCONNUE(400, "Difficulté inconnue", "..."),
    REPONSE_INATTENDUE(502, "Réponse inattendue d'un service distant", "..."),
    SERVICE_INJOIGNABLE(502, "Service distant injoignable", "...");
}
```

`CoursierException` (qui étend `BusinessException`) expose une fabrique par situation :
`contratIntrouvable()`, `reponseInattendue("La Guilde", 500)`… Le resource ne manipule jamais un
code HTTP.

### Première frontière : la réponse du service distant

`@ClientExceptionMapper` n'est appelé que pour les statuts 4xx et 5xx. Chaque statut a une
traduction, il n'y a pas de `return null` qui laisserait filer une `WebApplicationException` brute :

```java
@ClientExceptionMapper
static CoursierException versException(Response response) {
    if (response.getStatus() == 404) {
        return CoursierException.contratIntrouvable();
    }
    return CoursierException.reponseInattendue("La Guilde", response.getStatus());
}
```

Une erreur distante que l'on ne sait pas traduire n'est pas la faute de l'appelant : le coursier a
servi de passerelle, d'où le **502 Bad Gateway**. Le message interne de la Guilde n'est pas relayé,
seuls le nom du service et son statut le sont, dans `additional`.

Le contrat n°13 sert de démonstration : il est maudit, la Guilde répond 500.

### Pas de réponse du tout

Quand le service ne répond pas (connexion refusée, délai dépassé), il n'y a pas de `Response` à
traduire : le client lève une `ProcessingException`. `ServiceInjoignableExceptionMapper` la
transforme en 502 `SERVICE_INJOIGNABLE`. La cause technique (hôte, port) part dans les logs, pas
dans la réponse.

### Seconde frontière : la réponse de la taverne

| Mapper | Attrape | Produit |
| :--- | :--- | :--- |
| `BusinessExceptionMapper` | `CoursierException` | statut de l'erreur, `type` = `urn:problem:coursier:<CODE>`, `additional.code` |
| `ServiceInjoignableExceptionMapper` | `ProcessingException` | 502 `SERVICE_INJOIGNABLE` |
| `GlobalExceptionMapper` | tout le reste | 500 (ou le statut d'une `WebApplicationException`) |

Tous héritent d'`AbstractExceptionMapper`, qui ajoute `instance` et `timestamp`. Exemple :

```bash
curl -s localhost:8088/coursier/contrats/13
```

```json
{
  "type": "urn:problem:coursier:REPONSE_INATTENDUE",
  "title": "Réponse inattendue d'un service distant",
  "status": 502,
  "detail": "La Guilde a répondu 500 au coursier, qui revient les mains vides.",
  "instance": "http://localhost:8088/coursier/contrats/13",
  "timestamp": "2026-11-17T08:30:00.000Z",
  "additional": { "code": "REPONSE_INATTENDUE", "service": "La Guilde", "statutDistant": 500 }
}
```

Dernier détail : le filtre `?difficulte=` est lu comme une chaîne puis converti par
`Difficulte.depuis()`. Avec un enum directement en `@QueryParam`, JAX-RS répondrait 404 à une
valeur inconnue ; ici l'appelant reçoit un 400 `DIFFICULTE_INCONNUE` qui liste les valeurs possibles.

## 4. Le client programmatique

Toutes les APIs ne sont pas connues au build. Le Grimoire est ici un cas d'école : son adresse et
ses délais viennent de la configuration `taverne.grimoire.*`, lue par un `@ConfigMapping`, et le
client est construit à l'exécution.

```properties
taverne.grimoire.url=${GRIMOIRE_URL:http://localhost:8088}
taverne.grimoire.connect-timeout=1s
taverne.grimoire.read-timeout=2s
```

```java
@Produces
@Singleton
GrimoireClient grimoireClient() {
    return QuarkusRestClientBuilder.newBuilder()
            .baseUri(config.url())
            .connectTimeout(config.connectTimeout().toMillis(), TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeout().toMillis(), TimeUnit.MILLISECONDS)
            .build(GrimoireClient.class);
}

void fermer(@Disposes GrimoireClient client) throws IOException {
    client.close();
}
```

`GrimoireClient` reste une interface JAX-RS annotée, mais sans `@RegisterRestClient` : c'est le
builder qui fournit l'adresse. Elle étend `Closeable` : ce que l'on construit soi-même, on le ferme
soi-même, ici dans le disposer CDI. Cette approche est utile pour un multi-tenant, une découverte de
service ou une URL calculée au démarrage.

## 5. Ce que le coursier expose

| Méthode | Chemin | Rôle |
| :--- | :--- | :--- |
| `GET` | `/coursier/contrats` | liste les contrats, filtre optionnel `?difficulte=` (400 si inconnue) |
| `GET` | `/coursier/contrats/{id}` | lit un contrat, 404 si absent, 502 pour le contrat maudit n°13 |
| `POST` | `/coursier/contrats/{id}/acceptation?coursier=` | signe le contrat, transmet `X-Coursier`, rapporte un bordereau |
| `GET` | `/coursier/grimoire/{nom}` | consulte une recette via le client programmatique |

Les ressources `/guilde/...` et `/grimoire/...` sont les services simulés. Elles sont visibles dans
Swagger UI pour pouvoir les appeler à la main, mais elles ne font pas partie du client.

## 6. Tester

```bash
./mvnw -pl integration/rest-client-tutorial test
```

Les tests couvrent les deux clients et toutes les frontières d'erreur :

- `GuildeClientTest` injecte `@RestClient GuildeClient` et vérifie la liste, le filtre, la signature,
  le bordereau calculé, et la traduction du 404 et du 500 en `CoursierException` ;
- `CoursierResourceTest` passe par RestAssured sur les endpoints de la taverne et vérifie chaque
  `problem+json` : 400, 404, 502, route inconnue, ainsi que le contrat de la Guilde simulée ;
- `ServicesInjoignablesTest` utilise un `QuarkusTestProfile` qui fait pointer les deux clients sur
  un port fermé, et vérifie le 502 `SERVICE_INJOIGNABLE`.

Aucun conteneur, aucune dépendance externe : le module tourne seul, y compris en CI.

## 7. Ce que ce module ne couvre pas

- **Résilience** (retry, circuit breaker, fallback) : à combiner avec SmallRye Fault Tolerance, voir
  le module [La Taverne en Flammes](../../operations/la-taverne-en-flammes).
- **Propagation d'identité** (OIDC, JWT) : le client peut relayer un token, mais cela mérite son
  propre tutoriel.
- **Streaming et multipart** : le client Quarkus sait faire, ce module reste sur du JSON simple.
- **Client réactif** : `quarkus-rest-client` fonctionne aussi avec `Uni`/`Multi` ; voir le module
  [Programmation réactive](../../web-concept/reactive-programming-tutorial).

Le coursier sait désormais lire le tableau de la Guilde et le Grimoire sans écrire une seule ligne
de `HttpClient`. Il ne lui reste plus qu'à éviter le dragon.
