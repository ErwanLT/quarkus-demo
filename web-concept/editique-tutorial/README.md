# Éditique Tutorial - L'Imprimerie de la Taverne

À **The Falling Whale**, on ne se contente pas de servir des chopes : on remet un **ticket de caisse** à chaque aventurier et on archive proprement les **factures des fournisseurs** (le brasseur nain, le meunier du Val-Doré...).

Ce module montre comment générer ces documents avec **JasperReports 7** dans Quarkus grâce à l'extension Quarkiverse `quarkus-jasperreports`.

## Cas d'utilisation

| Document | Format | Ce qu'il illustre |
|---|---|---|
| Ticket de caisse | Rouleau thermique 80 mm (226 pt), une seule page qui s'allonge | Paramètres, `JRBeanCollectionDataSource`, variable de somme, `ignorePagination`, **composant list** (règlements) |
| Facture fournisseur | A4 | Rapport maître + **2 sous-rapports chargés de deux façons**, paramètre transmis, **valeur de retour**, `Page X / Y`, en-têtes répétés, export PDF **et** XLSX |
| Relevé des fournisseurs | A4 | Une bande par fournisseur, **composant list** sur une collection du champ courant, sous-totaux, total général |
| Étiquettes produits | A4, 3 × 4 | Composant **`barcode4j:EAN13`**, code calculé côté Java (clé de contrôle), planche de 12 étiquettes |
| Cartes de table | A4, 2 × 3 | Composant **`barcode4j:QRCode`**, une URL de menu différente par table |

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
3. Vérifiez depuis le téléphone que `http://192.168.1.42:8080/taverne/factures` répond. Sinon : même Wi-Fi (pas un réseau invité), pare-feu autorisant le port 8080, et Quarkus à l'écoute sur `0.0.0.0` (c'est la valeur par défaut, rappelée dans `application.properties`).
4. Régénérez les PDF : les QR déjà imprimés encodent l'ancienne URL.
   ```bash
   curl -o ticket.pdf http://192.168.1.42:8080/taverne/commandes/CMD-0042/ticket
   curl -o etiquettes.pdf http://192.168.1.42:8080/taverne/etiquettes
   curl -o cartes.pdf http://192.168.1.42:8080/taverne/tables/cartes
   ```
5. Scannez avec l'appareil photo : le QR ouvre la page de suivi ou le menu.

> Les PDF d'exemple livrés à la racine encodent `localhost` : ils fonctionnent sur le PC, pas depuis le téléphone. Régénérez-les avec votre IP LAN pour tester sur mobile.

**Astuce Android** : en USB, `adb reverse tcp:8080 tcp:8080` fait pointer `localhost:8080` du téléphone vers le PC. Les QR encodant `localhost` fonctionnent alors sans changer la configuration. Sur macOS, `http://nom-du-mac.local:8080` peut aussi fonctionner grâce à Bonjour, mais l'IP reste plus fiable.

Des exemples déjà générés sont disponibles à la racine du module (`ticket-CMD-0042.pdf`, `facture-FAC-BDF-2026-117.pdf`, `releve-fournisseurs.pdf`, `etiquettes-produits.pdf`, `cartes-tables.pdf`), ainsi que quelques variantes de tickets et de factures (`ticket-CMD-0043.pdf`, `facture-FAC-MVD-2026-031.pdf`, `facture-FAC-FCH-2026-008.pdf`, `facture-FAC-FCH-2026-012.pdf`).

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
<element kind="component" x="6" y="45" width="173" height="60">
    <component kind="barcode4j:EAN13" evaluationTime="Band" moduleWidth="2.0"
               textPosition="BOTTOM" quietZone="3.0">
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

- **`evaluationTime="Band"`** est obligatoire dès que l'expression lit un champ (`$F{...}`). La valeur par défaut (`Auto`) n'est pas supportée pour les codes-barres : le code n'est alors pas dessiné.
- Pour un **EAN-13**, `moduleWidth` est exprimé en points et non en millimètres. Avec `0.33`, le code ne faisait que ~49 pt de large, soit moins de 2 px par module à 300 dpi : illisible. `2.0` donne un code assez large pour être scanné.
- Le composant QR respecte le `forecolor` du style ; on force le **noir** pour garantir le contraste, même si la taverne préfère le brun.
- Les codes sont générés en **vectoriel** (formulaires PDF), pas en images bitmap. Les tests les rasterisent puis les relisent avec ZXing à **200 et 300 dpi** : les codes 1D fins passent mieux à 300 dpi, les QR denses rendus par PDFBox passent mieux à 200 dpi.

Les URL encodées sont construites à partir de `taverne.url-publique` :

```properties
taverne.url-publique=http://localhost:8080
```

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
- `ImprimerieResourceTest` : content-type, `Content-Disposition` (`inline` / `attachment`), 404, nouvelles routes `/etiquettes` et `/tables/cartes`.
