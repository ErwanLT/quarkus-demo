# Integration

Ce module regroupe les tutoriels sur l'integration de Quarkus avec des systemes, protocoles et flux externes.

## [Messaging](messaging)
Tutoriels autour des echanges asynchrones, des notifications et des flux d'evenements.

## [Le coursier de la taverne - REST Client](rest-client-tutorial)
Client REST déclaratif (`@RegisterRestClient`, `configKey`, `@ClientHeaderParam`) et programmatique
(`QuarkusRestClientBuilder`) : contrats de la Guilde, recettes du Grimoire, timeouts et traduction
des erreurs HTTP en `problem+json`.

## [L'Oracle au fond de la salle - LangChain4j](langchain4j-tutorial)
Intégration d'un LLM dans Quarkus avec `@RegisterAiService`, appel d'un service Panache comme outil et enrichissement des réponses par le grimoire des recettes (RAG).

### [Server-Sent Events - panneau magique](messaging/sse-tutorial)
Diffusion temps reel d'annonces de taverne avec RESTEasy Reactive, Mutiny et le media type `text/event-stream`.
