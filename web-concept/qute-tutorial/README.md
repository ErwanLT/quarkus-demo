# Qute Tutorial - Le Web avec Quarkus

Ce module démontre comment construire une application web complète avec **Qute**, l'alternative moderne de Quarkus à **Thymeleaf**, en utilisant la taverne **The Falling Whale** comme fil rouge.

## Concepts Clés

### 1. Checked Templates & Layouts
Nous utilisons les **Checked Templates** pour garantir la validité des paramètres au moment de la compilation.
*   **Layout Partagé** : Situé dans `src/main/resources/templates/tags/main.html`, il définit la structure commune (parchemin, navigation).
*   **Utilisation** : Les pages enfants utilisent la balise `{#main title=title}...{/main}` pour hériter du layout.

### 2. Persistance avec Hibernate Panache
Les réservations de chambres sont persistées dans une base de données **H2** (en mémoire) :
*   **Entité** : `Booking` hérite de `PanacheEntity` pour simplifier le CRUD.
*   **Transaction** : L'utilisation de `@Transactional` dans `TavernWebResource` permet de sauvegarder les données du formulaire de réservation.

### 3. Ressources Statiques
L'image de la taverne est servie via le dossier standard Quarkus :
`src/main/resources/META-INF/resources/img/tavern.png`
Elle est accessible dans les templates via le chemin relatif `/img/tavern.png`.

### 4. Gestion des Formulaires
Le formulaire de réservation utilise `@RestForm` pour capturer les entrées utilisateur et les transformer en objet `Booking` avant persistance.

## Utilisation

L'application est configurée pour démarrer sur le port **8082**.

1.  Lancez le mode développement :
    ```bash
    ./mvnw quarkus:dev
    ```
2.  **Se balader dans la taverne** :
    *   **Accueil** : [http://localhost:8082/tavern-web](http://localhost:8082/tavern-web)
    *   **Boissons** : [http://localhost:8082/tavern-web/drinks](http://localhost:8082/tavern-web/drinks)
    *   **Dormir** : [http://localhost:8082/tavern-web/booking](http://localhost:8082/tavern-web/booking)
    *   **Registre (Admin)** : [http://localhost:8082/tavern-web/admin](http://localhost:8082/tavern-web/admin)

## À retenir pour H2G2
*   Le prix du **Pan Galactic Gargle Blaster** est impérativement de **42 PO**.
*   N'oubliez jamais votre serviette.
