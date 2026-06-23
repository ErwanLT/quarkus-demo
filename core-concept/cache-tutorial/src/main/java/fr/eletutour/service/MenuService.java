package fr.eletutour.service;

import fr.eletutour.model.Plat;
import fr.eletutour.repository.CaveRepository;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Le tavernier derrière son comptoir.
 * <p>
 * Au-dessus de lui est suspendue l'<b>ardoise magique</b> ({@code quarkus-cache}).
 * Quand un aventurier demande le menu du jour, le tavernier lève d'abord
 * les yeux vers l'ardoise. Si le menu y est inscrit, il répond immédiatement
 * sans bouger. Sinon, il descend à la cave ({@link CaveRepository}) consulter
 * le grand livre, puis inscrit le résultat sur l'ardoise pour les prochains.
 * </p>
 *
 * <h2>Annotations utilisées</h2>
 * <ul>
 *   <li>{@code @CacheResult} : lève les yeux vers l'ardoise. Si le résultat
 *       est présent (cache hit), il est renvoyé directement. Sinon, la méthode
 *       s'exécute et le résultat est inscrit sur l'ardoise (cache miss).</li>
 *   <li>{@code @CacheInvalidate} : efface une entrée de l'ardoise. Le prochain
 *       appel redescendra obligatoirement à la cave.</li>
 *   <li>{@code @CacheInvalidateAll} : efface toute l'ardoise d'un coup.</li>
 * </ul>
 */
@ApplicationScoped
public class MenuService {

    private static final Logger LOG = Logger.getLogger(MenuService.class);

    /** Nom de l'ardoise (cache) partagée par toutes les opérations du menu. */
    static final String ARDOISE = "ardoise-menu";

    @Inject
    CaveRepository caveRepository;

    /**
     * Retourne le menu du jour pour un jour donné.
     * <p>
     * <b>Comportement de l'ardoise :</b>
     * <ul>
     *   <li><b>Cache hit</b> : le menu est sur l'ardoise → réponse immédiate,
     *       sans descente à la cave.</li>
     *   <li><b>Cache miss</b> : le menu n'est pas encore sur l'ardoise → descente
     *       à la cave, puis inscription sur l'ardoise pour les prochains visiteurs.</li>
     * </ul>
     * La clé de cache est automatiquement construite à partir du paramètre {@code jour}.
     * Deux jours différents correspondent donc à deux entrées distinctes sur l'ardoise.
     * </p>
     *
     * @param jour Le jour de la semaine (ex : "lundi")
     * @return La liste des plats du menu du jour
     */
    @CacheResult(cacheName = ARDOISE)
    public List<Plat> obtenirMenuDuJour(String jour) {
        LOG.infof("🍽️  L'ardoise ne connaît pas encore le menu de %s. Descente à la cave...", jour);
        return caveRepository.consulterGrandLivre(jour);
    }

    /**
     * Efface l'entrée d'un jour spécifique de l'ardoise.
     * <p>
     * À utiliser quand le chef change le menu en cours de journée. Le prochain
     * aventurier qui demandera ce jour-là forcera une nouvelle descente à la cave,
     * et le nouveau menu sera inscrit sur l'ardoise.
     * </p>
     *
     * @param jour Le jour dont l'entrée doit être effacée de l'ardoise
     */
    @CacheInvalidate(cacheName = ARDOISE)
    public void effacerMenuDuJour(String jour) {
        LOG.infof("🧽 Le tavernier efface %s de l'ardoise.", jour);
    }

    /**
     * Efface entièrement l'ardoise.
     * <p>
     * À utiliser en début de semaine pour forcer la relecture du grand livre
     * pour tous les jours. Après cet appel, la prochaine demande pour chaque
     * jour redescendra à la cave.
     * </p>
     */
    @CacheInvalidateAll(cacheName = ARDOISE)
    public void effacerTouteArdoise() {
        LOG.info("🧹 Le tavernier efface toute l'ardoise pour une nouvelle semaine.");
    }
}
