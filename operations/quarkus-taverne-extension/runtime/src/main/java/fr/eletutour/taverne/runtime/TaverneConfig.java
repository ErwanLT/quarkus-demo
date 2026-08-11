package fr.eletutour.taverne.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Les runes gravées dans la pierre : la configuration de la Taverne.
 *
 * <p>On reste en RUN_TIME ici : rien n'empêche le tavernier de changer d'humeur
 * (ou de couper le son) sans reconstruire le royaume.
 */
@ConfigMapping(prefix = "taverne")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TaverneConfig {

    /**
     * Active ou désactive les annonces du tavernier.
     */
    @WithDefault("true")
    boolean enabled();

    /**
     * Le ton pris par le tavernier pour annoncer chaque quête.
     */
    @WithDefault("BARDE")
    Ton ton();

    enum Ton {
        BARDE,
        GROGNON,
        SOBRE
    }
}
