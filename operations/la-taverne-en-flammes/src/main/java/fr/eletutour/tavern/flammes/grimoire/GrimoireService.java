package fr.eletutour.tavern.flammes.grimoire;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;

/**
 * Consultation du grimoire, protegee par MicroProfile Fault Tolerance.
 *
 * <p>Version declarative du meme principe que la cuisine reactive : borner l'attente pour
 * proteger les threads de l'application, et prevoir une issue de secours plutot qu'une erreur
 * 500. Les valeurs sont surchargeables par configuration, par exemple
 * {@code fr.eletutour.tavern.flammes.grimoire.GrimoireService/recupererRecette/Timeout/value}.</p>
 */
@ApplicationScoped
public class GrimoireService {

    private static final Logger LOG = Logger.getLogger(GrimoireService.class);

    @Inject
    GrimoireDistant grimoireDistant;

    @Timeout(value = 1500, unit = ChronoUnit.MILLIS)
    @Retry(maxRetries = 1, delay = 100, delayUnit = ChronoUnit.MILLIS)
    @Fallback(fallbackMethod = "recetteDeSecours")
    public Recette recupererRecette(String nom) {
        String platDemande = nomOuDefaut(nom);
        String texte = grimoireDistant.recuperer(platDemande);
        return new Recette(platDemande, texte, Recette.ORIGINE_GRIMOIRE, grimoireDistant.tentatives());
    }

    Recette recetteDeSecours(String nom) {
        String platDemande = nomOuDefaut(nom);
        LOG.warnf("Grimoire injoignable, on sort la recette de memoire : nom=%s, tentatives=%d",
            platDemande, grimoireDistant.tentatives());
        return new Recette(platDemande,
            "De memoire : " + platDemande + " au chaudron, comme le faisait la mere du tavernier.",
            Recette.ORIGINE_SECOURS, grimoireDistant.tentatives());
    }

    private String nomOuDefaut(String nom) {
        return (nom == null || nom.isBlank()) ? "tourte aux navets" : nom;
    }
}
