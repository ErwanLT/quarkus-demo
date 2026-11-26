package fr.eletutour.fitnesse;

import fitnesse.slim.fixtureInteraction.FixtureInteraction;
import fitnesse.slim.fixtureInteraction.InteractionAwareFixture;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Fixture Slim qui pilote un vrai navigateur. Elle implémente
 * InteractionAwareFixture pour intercepter TOUS les appels de Slim : dès qu'une
 * méthode lève une exception, on capture l'écran et on renvoie un message HTML
 * que FitNesse affiche directement dans la cellule en rouge.
 */
public class TaverneUiFixture implements InteractionAwareFixture {

    private WebDriver driver;

    @Override
    public Object aroundSlimInvoke(FixtureInteraction interaction, Method method, Object... arguments)
            throws Throwable {
        try {
            return interaction.methodInvoke(method, this, arguments);
        } catch (Throwable e) {
            Throwable cause = e instanceof InvocationTargetException && e.getCause() != null
                    ? e.getCause() : e;
            String html = captureScreenshot(cause);
            quitter();
            // FitNesse affiche le contenu de "message:<<...>>" comme du HTML
            // dans la cellule, au lieu d'un stack trace brut.
            throw new IllegalStateException("message:<<" + html + ">>");
        }
    }

    public void ouvrir(String url) {
        driver().get(url);
    }

    public String titre() {
        return driver().getTitle();
    }

    public void entrerDans(String valeur, String selecteur) {
        var element = driver().findElement(By.cssSelector(selecteur));
        element.clear();
        element.sendKeys(valeur);
    }

    public void cliquer(String selecteur) {
        driver().findElement(By.cssSelector(selecteur)).click();
    }

    public String texte(String selecteur) {
        return driver().findElement(By.cssSelector(selecteur)).getText();
    }

    /** Vérification "métier" : lève une exception pour que le screenshot soit joint. */
    public void verifierVaut(String selecteur, String attendu) {
        String reel = texte(selecteur);
        if (!attendu.equals(reel)) {
            throw new AssertionError("Attendu : " + attendu + " | Obtenu : " + reel);
        }
    }

    public void fermer() {
        quitter();
    }

    private WebDriver driver() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-gpu",
                    "--disable-dev-shm-usage", "--window-size=1280,800");
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

    private String captureScreenshot(Throwable cause) {
        String message = echapper(cause.getMessage() == null ? cause.toString() : cause.getMessage());
        try {
            byte[] png = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BYTES);
            String nom = "echec-" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));

            Path dossier = Path.of("target", "fitnesse", "FitNesseRoot", "files", "screenshots");
            Files.createDirectories(dossier);
            Files.write(dossier.resolve(nom + ".png"), png);

            String base64 = Base64.getEncoder().encodeToString(png);
            return "<div><strong>" + message + "</strong><br/>"
                    + "<a href=\"files/screenshots/" + nom + ".png\" target=\"_blank\">Ouvrir le PNG</a><br/>"
                    + "<img src=\"data:image/png;base64," + base64
                    + "\" alt=\"Screenshot de l'échec\" style=\"max-height:240px;border:1px solid #ccc\"/>"
                    + "</div>";
        } catch (Exception e) {
            return "<div><strong>" + message + "</strong><br/>Screenshot indisponible : "
                    + echapper(e.toString()) + "</div>";
        }
    }

    private String echapper(String texte) {
        return texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
