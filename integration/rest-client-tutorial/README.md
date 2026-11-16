# Le coursier de la taverne : consommer une API avec le REST Client

*La métaphore : la Taverne ne produit pas tout ce qu'elle sert. Chaque matin, un coursier part
récupérer les contrats affichés par la Guilde des aventuriers et les recettes consignées dans le
Grimoire. Plutôt que de lui faire écrire des requêtes HTTP à la main, on lui remet un **mandat** :
une interface Java décrit les gestes autorisés, Quarkus se charge du transport.*

Ce module montre comment consommer une API REST avec Quarkus :

- un client **déclaratif** (MicroProfile REST Client) injecté comme n'importe quel bean ;
- un client **programmatique** (`QuarkusRestClientBuilder`) quand l'adresse n'est connue qu'à l'exécution ;
- la configuration par `configKey`, les en-têtes, les timeouts et la traduction des erreurs HTTP.

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
```

## 1. Le mandat : un client déclaratif

`GuildeClient` ne contient aucune implémentation. Il décrit les chemins, les paramètres et les
en-têtes ; Quarkus génère le code HTTP au build.

```java
@Path("/guilde/contrats")
@RegisterRestClient(configKey = "guilde")
@ClientHeaderParam(name = "X-Taverne", value = "The Falling Whale")
public interface GuildeClient {

    @GET
    List<Contrat> contrats(@QueryParam("difficulte") Difficulte difficulte);

    @GET
    @Path("/{id}")
    Contrat contrat(@PathParam("id") long id);

    @POST
    @Path("/{id}/acceptation")
    ContratAccepte accepter(@PathParam("id") long id, @HeaderParam("X-Coursier") String coursier);
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
- `@ClientHeaderParam` ajoute un en-tête **constant** à tous les appels, ici le sceau de la taverne.
  Un en-tête variable se passe en `@HeaderParam` sur la méthode, comme `X-Coursier`.

La Guilde simulée refuse les appels sans `X-Taverne` : c'est la preuve, côté serveur, que le client
ajoute bien l'en-tête.

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

Par défaut, un 404 du serveur remonte en `WebApplicationException`, ce qui oblige la couche métier
à connaître HTTP. `@ClientExceptionMapper` permet de traduire la réponse au plus près du client :

```java
@ClientExceptionMapper
static ContratIntrouvableException versException(Response response) {
    if (response.getStatus() == 404) {
        return new ContratIntrouvableException("La Guilde ne connaît pas ce contrat.");
    }
    return null;
}
```

Le resource ne voit plus qu'une exception métier. Un `ExceptionMapper` JAX-RS la rend ensuite au
client de la taverne en `application/problem+json` :

```java
@Provider
public class ContratIntrouvableExceptionMapper implements ExceptionMapper<ContratIntrouvableException> {
    @Override
    public Response toResponse(ContratIntrouvableException exception) {
        return Probleme.reponse(404, "Contrat introuvable", exception.getMessage());
    }
}
```

Il y a donc **deux frontières** distinctes :

1. client → service distant : le 404 devient `ContratIntrouvableException` ;
2. taverne → consommateur : l'exception devient un problème RFC 9457.

## 4. Le client programmatique

Toutes les APIs ne sont pas connues au build. Le Grimoire est ici un cas d'école : son adresse vient
de la configuration `taverne.grimoire.url` et le client est construit à l'exécution.

```java
@Produces
@Singleton
GrimoireClient grimoireClient() {
    return QuarkusRestClientBuilder.newBuilder()
            .baseUri(baseUri)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .build(GrimoireClient.class);
}
```

`GrimoireClient` reste une interface JAX-RS annotée, mais sans `@RegisterRestClient` : c'est le
builder qui fournit l'adresse. Cette approche est utile pour un multi-tenant, une découverte de
service ou une URL calculée au démarrage.

## 5. Ce que le coursier expose

| Méthode | Chemin | Rôle |
| :--- | :--- | :--- |
| `GET` | `/coursier/contrats` | liste les contrats, filtre optionnel `?difficulte=` |
| `GET` | `/coursier/contrats/{id}` | lit un contrat, 404 problem+json si absent |
| `POST` | `/coursier/contrats/{id}/acceptation?coursier=` | signe le contrat et transmet `X-Coursier` |
| `GET` | `/coursier/grimoire/{nom}` | consulte une recette via le client programmatique |

Les ressources `/guilde/...` et `/grimoire/...` sont les services simulés. Elles sont visibles dans
Swagger UI pour pouvoir les appeler à la main, mais elles ne font pas partie du client.

## 6. Tester

```bash
./mvnw -pl integration/rest-client-tutorial test
```

Les tests couvrent les deux clients :

- `GuildeClientTest` injecte `@RestClient GuildeClient` et vérifie la liste, le filtre, la signature
  et la traduction du 404 en exception métier ;
- `CoursierResourceTest` passe par RestAssured sur les endpoints de la taverne et vérifie le
  `problem+json` final, ainsi que le refus de la Guilde sans `X-Taverne`.

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
