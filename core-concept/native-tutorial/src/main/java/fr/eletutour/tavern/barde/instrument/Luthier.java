package fr.eletutour.tavern.barde.instrument;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Locale;

/**
 * Le luthier fabrique l'instrument demandé par un aventurier.
 *
 * <p>Il retrouve la classe à partir de son <strong>nom</strong> (une chaîne de caractères),
 * puis l'instancie par réflexion. Sur la JVM, aucun problème : toutes les classes du
 * classpath sont disponibles. En natif, GraalVM ne peut pas deviner au build quelle chaîne
 * arrivera dans {@code Class.forName(...)} : sans enregistrement explicite pour la réflexion
 * (voir {@code NativeConfiguration}), les instruments sont élagués du binaire.</p>
 */
@ApplicationScoped
public class Luthier {

    private static final String ATELIER = "fr.eletutour.tavern.barde.instrument.";

    public Instrument fabriquer(String nom) {
        String nomDeClasse = ATELIER + capitaliser(nom);
        try {
            Class<?> plan = Class.forName(nomDeClasse);
            if (!Instrument.class.isAssignableFrom(plan)) {
                throw new InstrumentInconnuException(nom);
            }
            return (Instrument) plan.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new InstrumentInconnuException(nom, e);
        }
    }

    private static String capitaliser(String nom) {
        String minuscule = nom.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(minuscule.charAt(0)) + minuscule.substring(1);
    }
}
