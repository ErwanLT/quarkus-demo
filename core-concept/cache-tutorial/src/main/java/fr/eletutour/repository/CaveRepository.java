package fr.eletutour.repository;

import fr.eletutour.model.Plat;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * La cave du tavernier : source de vérité pour les plats et leurs prix.
 * <p>
 * Descendre à la cave pour consulter le grand livre prend du temps.
 * C'est pourquoi le tavernier préfère lever les yeux vers son ardoise
 * magique plutôt que de répéter ce voyage à chaque commande.
 * </p>
 * <p>
 * En production, cette classe ferait une vraie requête SQL ou un appel
 * réseau. Ici, un délai artificiel de 200 ms simule ce coût.
 * </p>
 */
@ApplicationScoped
public class CaveRepository {

    private static final Logger LOG = Logger.getLogger(CaveRepository.class);

    /** Le grand livre des recettes, stocké dans la cave. */
    private static final Map<String, List<Plat>> GRAND_LIVRE = Map.of(
            "lundi", List.of(
                    new Plat("Ragoût de sanglier", "Mijoté toute la nuit avec des herbes des bois", 8.5),
                    new Plat("Pain de seigle", "Cuit au feu de bois, idéal pour saucer", 1.5)
            ),
            "mardi", List.of(
                    new Plat("Soupe de légumes", "Poireaux, carottes et navets du potager", 4.0),
                    new Plat("Fromage affiné", "Affiné 3 mois dans la cave, servi avec du miel", 5.5)
            ),
            "mercredi", List.of(
                    new Plat("Poulet rôti à l'ail", "Rôti lentement sur broche, servi entier", 10.0),
                    new Plat("Pommes de terre au lard", "Sautées dans la graisse du poulet", 3.0)
            ),
            "jeudi", List.of(
                    new Plat("Poisson de rivière", "Pêché ce matin, grillé au beurre de ciboulette", 9.0),
                    new Plat("Légumes marinés", "Concombres et oignons dans le vinaigre de la maison", 2.5)
            ),
            "vendredi", List.of(
                    new Plat("Tarte aux champignons", "Cèpes et girolles ramassés en forêt", 7.0),
                    new Plat("Cidre chaud épicé", "Le meilleur remède contre le froid de l'hiver", 3.5)
            )
    );

    /**
     * Descend à la cave consulter le grand livre pour un jour donné.
     * <p>
     * Cette opération coûteuse simule 200 ms de latence. Sans l'ardoise
     * magique, chaque aventurier qui demande le menu entraînerait ce délai.
     * </p>
     *
     * @param jour Le jour de la semaine (ex : "lundi")
     * @return La liste des plats du jour, ou vide si le jour est inconnu
     */
    public List<Plat> consulterGrandLivre(String jour) {
        LOG.infof("📖 Le tavernier descend à la cave consulter le grand livre pour : %s", jour);
        simulerDelaiCave();
        return Optional.ofNullable(GRAND_LIVRE.get(jour.toLowerCase()))
                .orElse(List.of());
    }

    /**
     * Simule le temps nécessaire pour descendre à la cave et remonter.
     */
    private void simulerDelaiCave() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
