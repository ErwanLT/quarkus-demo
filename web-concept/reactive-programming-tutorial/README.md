# Reactive Programming Tutorial - Le Tavernier aux huit bras

Dans une taverne classique, un tavernier bloque devant le fût pendant que la bière coule. Pendant ce temps, il ne sert personne d'autre : le thread est occupé à attendre.

Dans une taverne réactive, le **tavernier aux huit bras** lance la pression, va servir une table, puis revient exactement quand la chope est pleine. Le travail n'est pas plus magique : il est simplement organisé autour d'événements futurs plutôt qu'autour d'attentes bloquantes.

Ce module montre la différence entre une approche impérative et une approche réactive avec Mutiny, la bibliothèque réactive utilisée par Quarkus :

* `Uni<T>` représente une seule valeur disponible plus tard, comme une chope qui sera pleine dans quelques instants.
* `Multi<T>` représente un flux de valeurs, comme une file continue de clients qui arrivent au comptoir.

## Cas d'utilisation

L'API expose trois démonstrations :

* `GET /taverne/reactif/pression/{aventurier}` : retourne un `Uni<BeerResponse>`. La réponse arrive après le temps de service demandé, sans bloquer physiquement le thread.
* `GET /taverne/reactif/pression/simultanee` : sert plusieurs aventuriers en même temps avec `Uni.join().all(...)`. C'est la preuve par l'exemple : le temps total observé se rapproche du plus long des services, jamais de leur somme.
* `GET /taverne/reactif/clients` : retourne un `Multi<ClientOrderResponse>` en Server-Sent Events. Chaque client apparaît comme un événement du flux.
* `GET /taverne/reactif/tournee` : collecte le même `Multi` dans une liste pour faciliter les tests et la lecture avec `curl`.
* `GET /taverne/reactif/comparaison` : résume la différence de mentalité entre impératif et réactif.

## Comment tester ?

1. Démarrer la taverne réactive :

```bash
cd web-concept/reactive-programming-tutorial
../../mvnw quarkus:dev
```

2. Demander une pinte future avec `Uni` :

```bash
curl "http://localhost:8080/taverne/reactif/pression/Gimli?durationMs=300"
```

3. Prouver que le tavernier ne bloque jamais un bras pour un seul client, en chronométrant trois chopes servies en même temps :

```bash
time curl "http://localhost:8080/taverne/reactif/pression/simultanee?aventuriers=Gimli,Legolas,Frodon&durationMs=500"
```

Le temps mesuré tourne autour de 500 ms, pas de 1500 ms : les trois `Uni` sont assemblés avec `Uni.join().all(...)` et progressent en parallèle plutôt qu'à la suite.

4. Observer un flux continu de clients avec `Multi` :

```bash
curl -N "http://localhost:8080/taverne/reactif/clients?count=5&intervalMs=250"
```

Interrompre la commande avec `Ctrl+C` avant la fin déclenche `onCancellation()` côté service : le tavernier arrête simplement de servir ce client, il ne reste pas figé en attente d'un flux que plus personne n'écoute.

5. Lire le même flux collecté en JSON :

```bash
curl "http://localhost:8080/taverne/reactif/tournee?count=3&intervalMs=50"
```

## Ce qu'il faut retenir

En impératif, le code décrit souvent une suite d'actions où chaque étape attend la précédente (exemple illustratif, cette méthode bloquante n'existe pas dans ce module) :

```java
// version imaginaire, a but pedagogique uniquement
BeerResponse beer = service.pourBeerBlocking("Gimli");
return beer;
```

En réactif, le code décrit une valeur future et la transformation qui devra s'appliquer quand elle sera disponible :

```java
Uni<BeerResponse> beer = service.pourBeer("Gimli", 300);
return beer;
```

Pour un seul résultat à venir, on utilise `Uni`. Pour une série de résultats au fil du temps, on utilise `Multi`.