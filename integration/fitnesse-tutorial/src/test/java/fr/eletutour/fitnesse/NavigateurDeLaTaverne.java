package fr.eletutour.fitnesse;

import fitnesse.slim.fixtureInteraction.FixtureInteraction;
import fitnesse.slim.fixtureInteraction.InteractionAwareFixture;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Fixture Slim qui pilote un vrai Chrome (headless). Ses méthodes sont
 * volontairement génériques (saisir, cliquer, lire un texte) : c'est la page
 * ScenarioLibrary qui les assemble en phrases métier.
 *
 * <p>Elle implémente {@link InteractionAwareFixture} pour intercepter tous les
 * appels de Slim : dès qu'une méthode lève une exception, on prend une capture
 * d'écran et on renvoie un message HTML que FitNesse affiche dans la cellule
 * en rouge.</p>
 */
public class NavigateurDeLaTaverne implements InteractionAwareFixture {

    /**
     * FitNesse sert le répertoire files/ de sa racine wiki : on y range les
     * captures pour que le lien « Ouvrir le PNG » fonctionne (ignoré par Git).
     */
    private static final Path CAPTURES = Path.of("src", "test", "fitnesse", "FitNesseRoot", "files", "captures");

    private WebDriver driver;

    @Override
    public Object aroundSlimInvoke(FixtureInteraction interaction, Method method, Object... arguments)
            throws Throwable {
        try {
            return interaction.methodInvoke(method, this, arguments);
        } catch (Throwable e) {
            Throwable cause = e instanceof InvocationTargetException && e.getCause() != null
                    ? e.getCause() : e;
            String html = rapportDEchec(cause);
            quitter();
            // FitNesse affiche le contenu de "message:<<...>>" comme du HTML
            // dans la cellule, au lieu d'une pile d'appels brute.
            throw new IllegalStateException("message:<<" + html + ">>");
        }
    }

    public void ouvrir(String url) {
        driver().get(url);
        attendreLaFinDesChargements();
    }

    public String titreDeLaPage() {
        return driver().getTitle();
    }

    public void saisirDans(String valeur, String selecteur) {
        WebElement champ = element(selecteur);
        champ.clear();
        champ.sendKeys(valeur);
        attendreLaFinDesChargements();
    }

    public void choisirDans(String valeur, String selecteur) {
        new Select(element(selecteur)).selectByValue(valeur);
        attendreLaFinDesChargements();
    }

    public void cocherSi(String selecteur, String ouiNon) {
        WebElement caseACocher = element(selecteur);
        if (caseACocher.isSelected() != ClientTaverne.ouiNon(ouiNon)) {
            caseACocher.click();
        }
        attendreLaFinDesChargements();
    }

    public void cliquerSur(String selecteur) {
        element(selecteur).click();
        attendreLaFinDesChargements();
    }

    public String texteDe(String selecteur) {
        return element(selecteur).getText();
    }

    public int nombreDe(String selecteur) {
        return driver().findElements(By.cssSelector(selecteur)).size();
    }

    /**
     * Vérification qui lève une exception plutôt que de renvoyer une valeur :
     * c'est ce qui déclenche la capture d'écran jointe au résultat.
     */
    public void verifierQueAffiche(String selecteur, String attendu) {
        String obtenu = texteDe(selecteur);
        if (!attendu.equals(obtenu)) {
            throw new AssertionError("Attendu : « " + attendu + " » | Obtenu : « " + obtenu + " »");
        }
    }

    /** Pour une ligne {@code | show | capture d'ecran |} : une vignette dans le compte rendu. */
    public String captureDEcran() throws Exception {
        String nom = enregistrer(capture(), "vue");
        return "<a href=\"files/captures/" + nom + "\" target=\"_blank\">"
                + "<img src=\"files/captures/" + nom + "\" alt=\"Capture de la taverne\""
                + " style=\"max-height:220px;border:1px solid #ccc\"/></a>";
    }

    public void fermer() {
        quitter();
    }

    private WebElement element(String selecteur) {
        return driver().findElement(By.cssSelector(selecteur));
    }

    /**
     * La page incrémente {@code body[data-chargements]} pendant chaque appel
     * fetch : on attend qu'il revienne à zéro plutôt que de dormir au hasard.
     */
    private void attendreLaFinDesChargements() {
        new WebDriverWait(driver(), Duration.ofSeconds(5)).until(d ->
                "0".equals(d.findElement(By.tagName("body")).getAttribute("data-chargements")));
    }

    private WebDriver driver() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-gpu",
                    "--disable-dev-shm-usage", "--window-size=1280,1100", "--lang=fr-FR");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        }
        return driver;
    }

    private void quitter() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (WebDriverException ignored) {
                // Le navigateur a déjà disparu : rien à fermer.
            } finally {
                driver = null;
            }
        }
    }

    private byte[] capture() {
        return ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BYTES);
    }

    private String enregistrer(byte[] png, String prefixe) throws Exception {
        String nom = prefixe + "-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")) + ".png";
        Files.createDirectories(CAPTURES);
        Files.write(CAPTURES.resolve(nom), png);
        return nom;
    }

    private String rapportDEchec(Throwable cause) {
        String message = echapper(cause.getMessage() == null ? cause.toString() : cause.getMessage());
        if (driver == null) {
            return "<div><strong>" + message + "</strong></div>";
        }
        try {
            byte[] png = capture();
            String nom = enregistrer(png, "echec");
            return "<div><strong>" + message + "</strong><br/>"
                    + "<a href=\"files/captures/" + nom + "\" target=\"_blank\">Ouvrir le PNG</a><br/>"
                    + "<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(png)
                    + "\" alt=\"Capture de l'échec\" style=\"max-height:260px;border:1px solid #ccc\"/>"
                    + "</div>";
        } catch (Exception e) {
            return "<div><strong>" + message + "</strong><br/>Capture indisponible : "
                    + echapper(e.toString()) + "</div>";
        }
    }

    private String echapper(String texte) {
        return texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
