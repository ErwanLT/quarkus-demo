# L'Oracle au fond de la salle : LangChain4j & LLM dans Quarkus

**Métaphore : le miroir magique du barde.** Ce module installe l'intelligence directement dans la taverne : l'aventurier interroge l'oracle, qui peut consulter le grimoire des recettes.

Le grimoire est prérempli avec **15 recettes** : boissons fermentées, jus, infusions, boissons chaudes et options sans alcool. Plusieurs recettes partagent des ingrédients, ce qui permet d'observer la recherche multi-résultats.

## Concepts

- `@RegisterAiService` expose un service IA injectable dans les beans CDI.
- `RecipeTools` propose au modèle un outil (`@Tool`) qui consulte le dépôt Hibernate Panache. `@ToolBox` le réserve à la méthode `answer` : `/oracle/grimoire` reçoit son contexte directement et ne doit pas pouvoir appeler l'outil.
- `/oracle/grimoire` récupère d'abord les recettes correspondant aux termes de la question, puis transmet ces passages au modèle comme contexte : c'est un premier RAG lexical, pédagogique. Un véritable moteur sémantique remplace ensuite cette recherche par embeddings et base vectorielle.

## Démarrer

Le module utilise Ollama en local pour éviter une clé API. Installez Ollama et récupérez un modèle capable d'appeler des outils, par exemple `ollama pull qwen2.5:7b`, puis lancez :

```bash
./mvnw -pl integration/langchain4j-tutorial quarkus:dev
```

Exemples :

```bash
curl 'http://localhost:8087/oracle?q=Quelles boissons contiennent du miel ?'
curl 'http://localhost:8087/oracle/grimoire?q=gingembre'
curl 'http://localhost:8087/oracle/grimoire?q=citron'
```

Vous pouvez choisir un autre modèle Ollama avec `OLLAMA_MODEL`. Le tool calling demande un modèle d'au moins 7 à 8 milliards de paramètres : avec `llama3.2` (3B), le modèle appelle l'outil mais recopie le schéma au lieu du mot-clé (`{"type":"string"}`), et `/oracle` répond n'importe quoi. Les variantes `-coder` n'émettent pas d'appel d'outil structuré. `/oracle/grimoire`, lui, fonctionne même avec un petit modèle, car la récupération se fait côté Java. Le modèle sert les réponses et peut choisir d'appeler le tool `findRecipe` pour interroger Panache. L'endpoint `/oracle/grimoire` illustre séparément la récupération de contexte suivie de la génération.
