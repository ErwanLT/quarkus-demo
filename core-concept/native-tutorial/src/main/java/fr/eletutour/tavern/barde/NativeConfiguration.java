package fr.eletutour.tavern.barde;

import fr.eletutour.tavern.barde.instrument.Flute;
import fr.eletutour.tavern.barde.instrument.Luth;
import fr.eletutour.tavern.barde.instrument.Tambour;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Liste des classes que GraalVM doit conserver pour la réflexion.
 *
 * <p>Le {@code Luthier} les instancie via {@code Class.forName(...)} : aucune référence
 * statique ne permet au compilateur natif de les découvrir seul.</p>
 */
@RegisterForReflection(targets = {
        Luth.class,
        Flute.class,
        Tambour.class
})
public class NativeConfiguration {
}
