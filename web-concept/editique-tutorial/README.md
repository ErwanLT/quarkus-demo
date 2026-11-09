# Éditique Tutorial - L'Imprimerie de la Taverne

À **The Falling Whale**, on ne se contente pas de servir des chopes : on remet un **ticket de caisse** à chaque aventurier et on archive proprement les **factures des fournisseurs** (le brasseur nain, le meunier du Val-Doré...).

Ce module montre comment générer ces documents avec **JasperReports 7** dans Quarkus grâce à l'extension Quarkiverse `quarkus-jasperreports`.

## Cas d'utilisation

| Document | Format | Ce qu'il illustre |
|---|---|---|
| Ticket de caisse | Rouleau thermique 80 mm (226 pt), une seule page qui s'allonge | Paramètres, `JRBeanCollectionDataSource`, variable de somme, `ignorePagination`, **composant list** (règlements) |
| Facture fournisseur | A4 | Rapport maître + **2 sous-rapports chargés de deux façons**, paramètre transmis, **valeur de retour**, `Page X / Y`, en-têtes répétés, export PDF **et** XLSX |
| Relevé des fournisseurs | A4 | Une bande par fournisseur, **composant list** sur une collection du champ courant, sous-totaux, total général |
| Étiquettes produits | A4, 3 colonnes remplies ligne par ligne (`printOrder="Horizontal"`) | Composant **`barcode4j:EAN13`**, code interne (préfixe 20) calculé côté Java (clé de contrôle) |
| Cartes de table | A4, 2 × 3 | Composant **`barcode4j:QRCode`**, une URL de menu différente par table |

Tous ces documents partagent une **charte graphique** commune, écrite une seule fois dans des feuilles de styles JasperReports (`.jrtx`) : voir [La charte graphique](#la-charte-graphique-jrtx).

### Sous-rapport ou composant list ?

| Cas | Où | Comment |
|---|---|---|
| 1. Sous-rapport compilé passé en paramètre | Facture → lignes | Le Java charge `facture-lignes.jasper` et le passe dans `$P{SOUS_RAPPORT_LIGNES}` (type `JasperReport`) |
| 2. Sous-rapport résolu par son chemin | Facture → récap TVA | `<expression>"jasperreports/facture-recap-tva.jasper"</expression>` : JasperReports le trouve tout seul sur le classpath |
| 3. Paramètre transmis au sous-rapport | Facture → récap TVA | `<parameter name="NUMERO_FACTURE">` dans l'élément `subreport` |
| 4. Valeur de retour d'un sous-rapport | Facture ← lignes | `<returnValue toVariable="NB_LIGNES" subreportVariable="REPORT_COUNT"/>` affiche « Lignes facturées : 3 » |
| 5. Composant list sur un paramètre | Ticket → règlements | `dataset` « Paiements » + `datasetRun` alimenté par `$P{PAIEMENTS}` |
| 6. Composant list sur un champ | Relevé → factures de chaque fournisseur | `datasetRun` alimenté par `$F{factures}` |
| 7. Image résolue par son chemin | Ticket → logo | `<element kind="image">` avec `<expression>"logo-impression.jpg"</expression>` (classpath) |
| 8. Image passée en paramètre | Facture, relevé → logo | Le Java charge `logo-impression.jpg` une fois et le passe en `byte[]` dans `$P{LOGO}` |

**Règle simple :** le composant `list` suffit pour répéter quelques champs à l'intérieur du rapport, sans fichier en plus. Un sous-rapport devient utile quand le bloc a sa propre mise en page (en-tête de colonnes répété à chaque saut de page, summary...), qu'on veut le réutiliser ailleurs ou qu'il doit renvoyer une valeur au rapport maître.

## Comment tester ?

1. Démarrer la taverne :
```bash
cd web-concept/editique-tutorial
../../mvnw quarkus:dev
```

2. Récupérer les documents :
```bash
# Ticket de caisse (affiché dans le navigateur)
curl -o ticket.pdf http://localhost:8080/taverne/commandes/CMD-0042/ticket

# Liste des factures fournisseurs
curl http://localhost:8080/taverne/factures

# Facture du brasseur nain (PDF), puis en tableur
curl -o facture.pdf http://localhost:8080/taverne/factures/FAC-BDF-2026-117
curl -o facture.xlsx "http://localhost:8080/taverne/factures/FAC-BDF-2026-117?format=xlsx"

# Facture de 40 lignes du meunier : pagination sur 2 pages
curl -o meunerie.pdf http://localhost:8080/taverne/factures/FAC-MVD-2026-031

# Relevé mensuel de tous les fournisseurs
curl -o releve.pdf http://localhost:8080/taverne/fournisseurs/releve

# Planche d'étiquettes produits (EAN-13) et cartes de table (QR codes)
curl -o etiquettes.pdf http://localhost:8080/taverne/etiquettes
curl -o cartes.pdf http://localhost:8080/taverne/tables/cartes
```

Les QR codes imprimés pointent vers des pages HTML de suivi :

```bash
open http://localhost:8080/taverne/commandes/CMD-0042/suivi
open http://localhost:8080/taverne/factures/FAC-BDF-2026-117/suivi
open "http://localhost:8080/taverne/menu?table=Table%20du%20Dragon"
```

### Le comptoir de scan (codes-barres)

Un QR code contient une URL : le téléphone l'ouvre tout seul. Un **Code 128** ou un **EAN-13** ne contient qu'une valeur brute (`CMD-0042`, `2000000000015`) : il faut un comptoir qui sache ce qu'elle désigne.

```bash
# Page avec un champ de saisie : une douchette USB y tape le code puis Entrée
open http://localhost:8080/taverne/scan

# URL modèle pour les applis de scan capables d'ouvrir « http://…/taverne/scan/%s »
curl -i http://localhost:8080/taverne/scan/CMD-0042        # 303 -> suivi de la commande
curl -i http://localhost:8080/taverne/scan/2000000000015   # 303 -> fiche du Tonneau de rousse naine
curl -i http://localhost:8080/taverne/scan/2000000000016   # 404 : clé de contrôle invalide
```

`ComptoirDeScan` reconnaît les numéros de commande, les numéros de facture et les EAN-13. Pour un EAN-13, il **vérifie la clé de contrôle** avant de chercher le produit : un chiffre mal lu est refusé plutôt que d'ouvrir le mauvais produit. Un code reconnu redirige en `303 See Other` vers sa page ; un code inconnu reste au comptoir, avec un 404 et la raison du refus.

Avec un téléphone, une appli de scan généraliste (ou Google Lens) lit le code-barres et affiche sa valeur : il suffit de la coller dans le champ du comptoir, ou de configurer l'appli pour ouvrir l'URL modèle. Lire les codes-barres directement avec la caméra dans le navigateur (`BarcodeDetector`, zxing-js) demanderait de servir la taverne en **HTTPS** : `getUserMedia` refuse l'accès à la caméra sur une page en `http://ip:port`.

### Scanner les QR codes depuis un téléphone

Un QR code contient une URL, pas une adresse magique. Par défaut elle vaut `http://localhost:8080/...` : sur un téléphone, `localhost` désigne **le téléphone lui-même**, pas le PC. Le navigateur affiche donc une page vide ou une erreur.

1. Trouvez l'IP LAN du PC :
   - macOS : `ipconfig getifaddr en0`
   - Linux : `hostname -I`
   - Windows : `ipconfig`
2. Relancez Quarkus en donnant cette base d'URL :
   ```bash
   TAVERNE_URL_PUBLIQUE=http://192.168.1.42:8080 ../../mvnw quarkus:dev
   ```
   ou modifiez `taverne.url-publique` dans `application.properties`.
3. Vérifiez depuis le téléphone que `http://192.168.1.42:8080/taverne/factures` répond. Sinon : même Wi-Fi (pas un réseau invité), pare-feu autorisant le port 8080, et Quarkus à l'écoute sur `0.0.0.0`. En mode dev, Quarkus n'écoute que sur `localhost` par défaut : `application.properties` l'ouvre au réseau local avec `%dev.quarkus.http.host=0.0.0.0` (en prod, c'est déjà le défaut).
4. Régénérez les PDF : les QR déjà imprimés encodent l'ancienne URL.
   ```bash
   curl -o ticket.pdf http://192.168.1.42:8080/taverne/commandes/CMD-0042/ticket
   curl -o etiquettes.pdf http://192.168.1.42:8080/taverne/etiquettes
   curl -o cartes.pdf http://192.168.1.42:8080/taverne/tables/cartes
   ```
5. Scannez avec l'appareil photo : le QR ouvre la page de suivi ou le menu.

> Les PDF d'exemple livrés à la racine encodent `localhost` : ils fonctionnent sur le PC, pas depuis le téléphone. Régénérez-les avec votre IP LAN pour tester sur mobile.

**Astuce Android** : en USB, `adb reverse tcp:8080 tcp:8080` fait pointer `localhost:8080` du téléphone vers le PC. Les QR encodant `localhost` fonctionnent alors sans changer la configuration. Sur macOS, `http://nom-du-mac.local:8080` peut aussi fonctionner grâce à Bonjour, mais l'IP reste plus fiable.

Des exemples déjà générés sont disponibles à la racine du module (`ticket-CMD-0042.pdf`, `facture-FAC-BDF-2026-117.pdf`, `releve-fournisseurs.pdf`, `etiquettes-produits.pdf`, `cartes-tables.pdf`).

> ⚠️ En mode dev, les `.jrxml` ne sont pas recompilés à chaud : après une modification, relancez `quarkus:dev`.

Un numéro inconnu des archives renvoie un **404**.

## Configuration

### Compilation des rapports au build
Les `.jrxml` vivent dans `src/main/jasperreports/`. L'extension les compile en `.jasper` pendant le build Quarkus et les dépose sur le classpath dans `jasperreports/` : aucune compilation au démarrage.

```properties
quarkus.jasperreports.build.source=src/main/jasperreports
quarkus.jasperreports.build.destination=jasperreports
```

L'`ImprimerieService` charge ensuite chaque rapport compilé une seule fois (`JRLoader.loadObject`) et le garde en cache.

### Les sous-rapports
Le maître `facture-fournisseur.jrxml` fournit à chaque sous-rapport ses données à partir des champs de la facture :

```xml
<element kind="subreport" x="0" y="2" width="555" height="20">
    <expression><![CDATA[$P{SOUS_RAPPORT_LIGNES}]]></expression>
    <dataSourceExpression><![CDATA[new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($F{lignes})]]></dataSourceExpression>
    <returnValue toVariable="NB_LIGNES" subreportVariable="REPORT_COUNT"/>
</element>
```

### Le composant list
Pas de fichier à part : un `dataset` déclaré en tête du rapport, et un `datasetRun` qui l'alimente.

```xml
<dataset name="Paiements">
    <field name="mode" class="java.lang.String"/>
    <field name="montant" class="java.math.BigDecimal"/>
</dataset>
...
<element kind="component" x="0" y="46" width="210" height="12">
    <component kind="list" printOrder="Vertical">
        <datasetRun subDataset="Paiements">
            <dataSourceExpression><![CDATA[new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{PAIEMENTS})]]></dataSourceExpression>
        </datasetRun>
        <contents height="12">...</contents>
    </component>
</element>
```

### Le logo
Le blason de la taverne apparaît sur les trois documents, chargé de deux façons :

```xml
<!-- Ticket : JasperReports résout la chaîne sur le classpath -->
<element kind="image" x="0" y="0" width="210" height="62" scaleImage="RetainShape" hImageAlign="Center" usingCache="true">
    <expression><![CDATA["logo-impression.jpg"]]></expression>
</element>

<!-- Facture et relevé : octets transmis par le service -->
<parameter name="LOGO" class="byte[]"/>
...
<expression><![CDATA[$P{LOGO}]]></expression>
```

`scaleImage="RetainShape"` conserve les proportions de l'image dans son cadre.

Attention au poids : l'image est embarquée telle quelle dans chaque PDF. Le logo d'origine (`logo.png`, 1024 × 572 px, 736 Ko) faisait monter chaque document au-delà de 700 Ko. Les rapports utilisent donc `logo-impression.jpg`, une version recadrée sur l'écusson, réduite à 260 px et compressée en JPEG (30 Ko) : le ticket redescend sous les 90 Ko. Un test (`ticketLeger`) veille à ce que ça reste le cas.

### Codes-barres et QR codes
L'extension apporte **Barcode4J** et **ZXing** : aucune dépendance supplémentaire n'est nécessaire. Le principe reste le même que pour le logo : **le Java décide de ce qui est encodé**, JasperReports ne fait que le dessiner.

```xml
<!-- EAN-13 : la clé de contrôle est calculée par IdentificationService -->
<element kind="component" x="11" y="44" width="163" height="56">
    <component kind="barcode4j:EAN13" evaluationTime="Band" moduleWidth="1.4"
               textPosition="BOTTOM" quietZone="15.0">
        <codeExpression><![CDATA[$F{ean13}]]></codeExpression>
    </component>
</element>

<!-- QR code : une URL de suivi différente pour chaque table -->
<element kind="component" x="68" y="62" width="140" height="140" forecolor="#000000">
    <component kind="barcode4j:QRCode" evaluationTime="Band" margin="2" errorCorrectionLevel="M">
        <codeExpression><![CDATA[$F{url}]]></codeExpression>
    </component>
</element>
```

Quelques pièges rencontrés :

- Pour un **EAN-13**, `moduleWidth` est exprimé en points et non en millimètres. Avec `0.33`, le code ne faisait que ~49 pt de large, soit moins de 2 px par module à 300 dpi : illisible. Avec `2.0`, le code et ses zones de silence demandaient près de 200 pt dans un cadre de 173 pt : JasperReports l'a réduit pour le faire tenir (167 pt mesurés), et la zone de silence de gauche (3 pt, réduite elle aussi) n'avait plus la place d'accueillir le premier chiffre. `1.4` avec `quietZone="15.0"` donne 95 × 1,4 + 2 × 15 ≈ 163 pt : tout tient, chiffre compris.
- **Le composant ne s'aligne pas** : un code plus petit que son cadre est dessiné à sa largeur naturelle, collé à gauche ; un code plus grand est réduit. Pour centrer le Code 128 du ticket, le cadre est calé sur cette largeur puis centré à la main. `CMD-0042` fait 112 modules (Barcode4J passe automatiquement en jeu C, plus compact, pour la paire de chiffres `0042`) : 112 pt + 2 zones de silence de 10 pt = 132 pt, posés à `x="39"`.
- **`positionType="Float"`** : sur le ticket, les codes suivent le reste du `summary`. Sans ça, ils restent à leur position de conception pendant que les textes voisins descendent avec la liste des règlements, et tout se chevauche dès le deuxième paiement.
- **Préfixe EAN** : les codes de démonstration commencent par `20`. GS1 réserve les préfixes 20 à 29 à la circulation restreinte (codes internes à un magasin) : impossible de tomber sur le code d'un vrai produit, ce qui n'était pas le cas avec `357` (plage de GS1 France).
- Le composant QR respecte le `forecolor` du style ; on force le **noir** pour garantir le contraste, même si la taverne préfère le brun.
- **QR code en image matricielle** : par défaut, JasperReports dessine un QR en SVG, avec un rectangle par module. Une fois rasterisés, les bords anticrénelés de ces rectangles laissent des liserés blancs entre modules : les QR devenaient illisibles à 300 dpi (0 sur 6 pour les cartes de table) alors qu'ils passaient à 150 dpi. Les rapports qui contiennent des QR déclarent donc :
  ```xml
  <property name="net.sf.jasperreports.components.barcode4j.image.producer" value="image"/>
  ```
  Les codes sont alors produits en image (300 dpi par défaut) et se lisent à toutes les résolutions testées (150 à 400 dpi).
- Les tests rasterisent les PDF puis relisent les codes avec ZXing. Pour les cartes de table, la page est découpée en tuiles (une carte par tuile) et lue à **300 dpi**, la résolution d'impression : le lecteur multiple de ZXing peut manquer un code sur une planche de six.

Les pages HTML ciblées par les QR codes (suivi de commande, suivi de facture, menu d'une table) sont rendues avec des templates **Qute** type-safe (`@CheckedTemplate`, `templates/SuiviResource/`), qui échappent automatiquement les valeurs. Les montants y sont formatés comme dans les PDF (`1 378,69 po`) grâce à une extension de template (`FormatsTaverne`).

> En réel, une URL de suivi ne doit pas reposer sur un numéro séquentiel (`CMD-0042`) : n'importe qui pourrait consulter la commande suivante en changeant un chiffre. On encoderait un jeton non devinable (UUID, URL signée).

Les URL encodées sont construites à partir de `taverne.url-publique` :

```properties
taverne.url-publique=http://localhost:8080
```

### La charte graphique (`.jrtx`)
Aucun rapport ne choisit lui-même une police, une couleur, un trait ou un format de montant : tout vient de deux feuilles de styles placées à côté des `.jrxml`.

| Fichier | Rôle |
|---|---|
| `charte-taverne.jrtx` | La charte de The Falling Whale : palette (encre, sépia, parchemin…), typographie (`Titre`, `Etiquette`, `Note`…), formats (`Montant` en `#,##0.00 po`, `Taux`), fonds et filets (`Bandeau`, `Carte`, `FiletLeger`…), `Code` en noir pur pour les codes-barres |
| `charte-thermique.jrtx` | Sa déclinaison noir et blanc pour le rouleau du ticket : elle **inclut** la charte de la taverne et ne redéfinit que les styles qui changent |

L'extension **copie** les `.jrtx` à côté des `.jasper`, sans les compiler (« Copying … charte-taverne.jrtx » dans les logs du build). Chaque rapport, sous-rapports compris, charge sa charte au remplissage à partir d'un paramètre :

```xml
<template><![CDATA[$P{CHARTE}]]></template>
<parameter name="CHARTE" class="java.lang.String">
	<defaultValueExpression><![CDATA["jasperreports/charte-taverne.jrtx"]]></defaultValueExpression>
</parameter>
...
<element kind="textField" x="425" y="14" width="130" height="16" style="Montant">
```

L'enum `Charte` porte l'emplacement de chaque charte : `ImprimerieService` passe `THERMIQUE` au ticket et `TAVERNE` aux documents A4. Le maître de la facture transmet `CHARTE` à ses deux sous-rapports, qui ne voient pas ses styles.

Comportements vérifiés avec JasperReports 7.1.0 :
- un style déclaré dans le `.jrxml` l'emporte sur celui de même nom de la charte ; dans une charte, un style redéfini l'emporte sur celui de la charte incluse ;
- le style `default="true"` de la charte devient le style par défaut du rapport ;
- un `<template>` inclus dans un `.jrtx` chargé depuis le classpath doit porter son **chemin complet** (`jasperreports/charte-taverne.jrtx`) : un chemin relatif échoue (« Byte data not found ») ;
- **redéfinir `Base` ne repeint pas les styles de la charte incluse** : un style se rattache au parent de ce nom déclaré *avant* lui. La charte thermique redéfinit donc chacun des styles colorés employés par le ticket ;
- un `.jrtx` n'étant pas compilé, la condition d'un `conditionalStyle` ne peut y être qu'une **simple référence** (`$P{…}`, `$F{…}`, `$V{…}`). Le style `Zebre` lit `$V{LIGNE_PAIRE}`, une variable booléenne déclarée par `facture-lignes.jrxml` ; avec `$V{REPORT_COUNT} % 2 == 0` écrit dans la charte, le remplissage échoue (« is not a simple parameter/field/variable … reference ») ;
- un nom de style mal orthographié passe la compilation et n'échoue qu'au remplissage (« Style not found »).

Deux tests verrouillent la charte : `RapportsSousCharteTest` interdit toute mise en forme en dur dans les `.jrxml` et vérifie que chaque style employé existe ; `CharteGraphiqueTest` relit les couleurs réellement posées dans les PDF.

### Le modèle : des getters, pas des records
`JRBeanCollectionDataSource` lit les champs selon les conventions JavaBean (`getLibelle()`...). Les classes du package `model` sont donc des POJO à getters ; les montants calculés (`getMontant()`, `getTotalTtc()`, `getRecapTva()`) sont exposés comme des champs à part entière.

### Locale et polices
- `REPORT_LOCALE = Locale.FRANCE` : virgule décimale, et une **espace insécable fine** (U+202F) comme séparateur de milliers. Pensez-y dans vos tests sur le texte des PDF.
- Toutes les zones utilisent la police `DejaVu Sans`, fournie par `jasperreports-fonts` (embarqué par l'extension). Sans police embarquée, les accents et symboles dépendent des polices du système, et en mode headless on se heurte vite à l'erreur « font is not available to the JVM ».

### Format JRXML de la v7
JasperReports 7 a changé le format des `.jrxml` (`<element kind="textField">` au lieu de `<textField><reportElement>`). Les rapports écrits pour la v6 doivent être migrés, par exemple en les ouvrant puis en les enregistrant dans Jaspersoft Studio 7.

## Tests
- `CommandeTest`, `FactureFournisseurTest`, `ReleveFournisseurTest` : calculs du modèle (totaux, récap TVA, regroupement par fournisseur).
- `IdentificationServiceTest` : numéros de commande, clés EAN-13 (y compris un rejet de clé invalide), URLs de suivi et encodage des tables.
- `ImprimerieServiceTest` : texte des PDF extrait avec **PDFBox** (règlements, paramètre et valeur de retour des sous-rapports, relevé), nombre de pages, signature `PK` du XLSX, et **décodage ZXing** des codes-barres/QR codes (Code 128, EAN-13, QR).
- `RapportsSousCharteTest` (sans Quarkus, sur les sources) : aucun `<style>` local, aucun attribut `fontName`, `fontSize`, `forecolor`, `backcolor`, `bold`, `italic`, `pattern`… ni `<pen>` dans les `.jrxml` ; chaque rapport charge `$P{CHARTE}` ; chaque style employé existe dans sa charte.
- `CharteGraphiqueTest` : couleurs relevées dans le flux de contenu des PDF (opérateurs `rg`/`RG`) ; les documents A4 restent dans la palette, le ticket est en noir et blanc, les lignes de facture sont zébrées.
- `ImprimerieResourceTest` : content-type, `Content-Disposition` (`inline` / `attachment`), 404, nouvelles routes `/etiquettes` et `/tables/cartes`.
