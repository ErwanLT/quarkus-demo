# banner-tutorial

Il y a de cela quelque temps, nous avions pu voir [[Comment personnaliser la bannière d'un projet Spring Boot|comment personnaliser la bannière]] d'un projet [[Il était une fois... Spring Boot|Spring Boot]].  
Ici, nous verrons comment arriver au même résultat, mais dans un projet [[Quarkus - définition|Quarkus]].

## La bannière Quarkus par défaut

[![](https://www.sfeir.dev/content/images/2025/03/image-16.png)](https://www.sfeir.dev/content/images/2025/03/image-16.png)

bannière quarkus

Au démarrage de votre application Quarkus, vous devriez voir apparaitre dans les logs de votre application cette bannière, il s'agit de la bannière par défaut des applications Quarkus.

Tout comme la bannière d'un projet Spring Boot, cette dernière peut être désactivé ou personnalisable.

## Désactiver la bannière

Pour désactiver la bannière au démarrage d'un projet quarkus, plusieurs options s'offrent à vous :

- Les propriétés de votre projet
- Paramètre au démarrage de l'application

### Depuis le fichier de properties

Pour désactiver la bannière depuis votre fichier `application.properties`, vous n'avez qu'à rajouter la ligne suivante à l'intérieur :

```properties
quarkus.banner.enabled=false
```

vous n'aurez à ce moment plus la bannière qui s'affichera au démarrage de votre application.

### En paramètre de l'application

Si vous ne souhaitez pas rajouter de properties dans votre projet pour désactiver la bannière, vous pouvez également la désactiver en passant un paramètre lors du lancement de votre application.

Si vous utilisez maven pour lancer votre application :

```bash
quarkus:dev -Dquarkus.banner.enabled=false
```

run via maven

Si vous utilisez Quarkus CLI pour lancer votre application :

```bash
quarkus dev -Dquarkus.banner.enabled=false
```

Là aussi, la bannière par défaut ne s'affichera plus au démarrage.

## Personnaliser la bannière

Si au contraire, vous ne souhaitez pas désactiver la bannière, mais au contraire la personnaliser, rien de plus simple, il vous suffit de 2 étapes :

- Créer un fichier texte dans les ressources de votre projet
- Ajouter la propriété suivante qui indique le nom de votre fichier

```properties
quarkus.banner.path=banner.txt
```

Et ainsi au démarrage de votre application, le contenu de votre fichier texte s'affichera en guise de bannière.

```txt
Oh no not again !
     (")
     \)/
    _____
    \___/

                      Hello ground !!
        .-------------'```'----....,,__                        _,
       |                               `'`'`'`'-.,.__        .'(
       |                                             `'--._.'   )
       |                                                   `'-.<
       \               .-'`'-.                            -.    `\
        \               -.o_.     _                     _,-'`\    |
         ``````''--.._.-=-._    .'  \            _,,--'`      `-._(
           (^^^^^^^^`___    '-. |    \  __,,..--'                 `
            `````````   `'--..___\    |`
                                  `-.,'
```

exemple de bannière en ASCII art

Si j'ai le contenu ci-dessus dans mon fichier `banner.txt`, au démarrage de mon application j'aurai le rendu suivant dans les logs :

[![](https://www.sfeir.dev/content/images/2025/03/image-24.png)](https://www.sfeir.dev/content/images/2025/03/image-24.png)

rendu dans les logs

### Limitation de la personnalisation

Contrairement à la bannière Spring Boot qui supporte des styles de texte AINSI :

- ${AnsiStyle.STYLE_NAME} : BOLD / ITALIC / NORMAL ...
- ${AnsiColor.COLOR_NAME} : BLUE / RED / YELLOW ...
- ${AnsiBackground.COLOR_NAME}

Ces derniers ne sont pas supportés par la bannière Quarkus, vous n'aurez donc pas pour l'heure un rendu similaire à ceci

[![](https://www.sfeir.dev/content/images/2025/03/image-25.png)](https://www.sfeir.dev/content/images/2025/03/image-25.png)

## Conclusion

Vous savez désormais comment personnaliser ou désactiver la bannière d'un projet Quarkus.  
Internet regorge d'images en tout genre en ASCII art, ou de générateur de bannière, il vous suffit juste de trouver celle qui vous plaît, et le tour est joué.
