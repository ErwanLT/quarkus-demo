package fr.eletutour.tavern.service;

import fr.eletutour.tavern.dto.AventurierConnection;
import fr.eletutour.tavern.dto.AventurierEdge;
import fr.eletutour.tavern.dto.AventurierResponse;
import fr.eletutour.tavern.dto.PageInfo;
import fr.eletutour.tavern.model.Aventurier;
import fr.eletutour.tavern.model.Quete;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service gérant la logique métier de la taverne et de ses aventuriers.
 */
@ApplicationScoped
public class TaverneService {

    private static final String CURSOR_PREFIX = "aventurier:";

    private final List<Aventurier> aventuriers = new ArrayList<>();

    /**
     * Récupère la liste de tous les aventuriers présents dans la taverne.
     * Initialise des données de test si la liste est vide.
     *
     * @return une liste non nulle d'aventuriers.
     */
    public List<Aventurier> getAllAventuriers() {
        if (aventuriers.isEmpty()) {
            initData();
        }
        return List.copyOf(aventuriers);
    }

    /**
     * Récupère une tranche du registre, par index de départ et taille maximale.
     *
     * @param offset index du premier aventurier à servir (0 si négatif).
     * @param limit  nombre maximum d'aventuriers à servir (0 si négatif).
     * @return la tranche d'aventuriers correspondante, potentiellement vide.
     */
    public List<Aventurier> getAventuriers(int offset, int limit) {
        return getAllAventuriers().stream()
                .skip(Math.max(offset, 0))
                .limit(Math.max(limit, 0))
                .toList();
    }

    /**
     * Récupère une page du registre par curseur, façon Relay.
     *
     * @param after curseur après lequel reprendre la lecture, ou null pour repartir du début.
     * @param first nombre d'aventuriers à servir dans cette page.
     * @return la connection contenant les edges et les métadonnées de pagination.
     */
    public AventurierConnection getAventuriersConnection(String after, int first) {
        List<Aventurier> tous = getAllAventuriers();
        int start = decodeCursor(after);
        int end = Math.min(start + Math.max(first, 0), tous.size());

        List<AventurierEdge> edges = new ArrayList<>();
        for (int i = start; i < end; i++) {
            edges.add(new AventurierEdge(encodeCursor(i), AventurierResponse.fromDomain(tous.get(i))));
        }

        boolean hasNext = end < tous.size();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).cursor();

        return new AventurierConnection(edges, new PageInfo(hasNext, endCursor));
    }

    private String encodeCursor(int index) {
        return Base64.getEncoder().encodeToString((CURSOR_PREFIX + index).getBytes());
    }

    private int decodeCursor(String cursor) {
        if (cursor == null) {
            return 0;
        }
        String decoded = new String(Base64.getDecoder().decode(cursor));
        return Integer.parseInt(decoded.substring(CURSOR_PREFIX.length())) + 1;
    }

    /**
     * Regroupe les quêtes de plusieurs aventuriers en un seul passage,
     * pour éviter d'interroger le registre une fois par aventurier (problème N+1).
     * Aujourd'hui adossé à une simple liste en mémoire, cette méthode garde la même
     * signature le jour où le registre sera porté par une vraie base de données.
     *
     * @param aventurierIds les identifiants des aventuriers concernés.
     * @return une map identifiant -> liste de quêtes, avec une liste vide pour les aventuriers sans quête.
     */
    public Map<Long, List<Quete>> getQuetesForAventuriers(List<Long> aventurierIds) {
        return getAllAventuriers().stream()
                .filter(a -> aventurierIds.contains(a.id))
                .collect(Collectors.toMap(a -> a.id, a -> a.quetes != null ? a.quetes : List.of()));
    }

    private void initData() {
        Aventurier guerrier = new Aventurier();
        guerrier.id = 1L;
        guerrier.nom = "Baldric";
        guerrier.classe = "Guerrier";
        guerrier.niveau = 12;
        guerrier.soldeOr = 340;

        Quete quete = new Quete();
        quete.id = 100L;
        quete.titre = "Nettoyer les caves infestées";
        quete.difficulte = "Moyenne";
        quete.recompenseOr = 250;

        guerrier.quetes = List.of(quete);

        Aventurier archere = new Aventurier();
        archere.id = 2L;
        archere.nom = "Sylvane";
        archere.classe = "Archère";
        archere.niveau = 9;
        archere.soldeOr = 120;

        Quete queteArchere = new Quete();
        queteArchere.id = 101L;
        queteArchere.titre = "Escorter la caravane des marchands";
        queteArchere.difficulte = "Facile";
        queteArchere.recompenseOr = 80;

        archere.quetes = List.of(queteArchere);

        Aventurier nain = new Aventurier();
        nain.id = 3L;
        nain.nom = "Grendel";
        nain.classe = "Nain";
        nain.niveau = 15;
        nain.soldeOr = 610;

        Quete queteNain = new Quete();
        queteNain.id = 102L;
        queteNain.titre = "Rouvrir la mine effondrée";
        queteNain.difficulte = "Difficile";
        queteNain.recompenseOr = 400;

        nain.quetes = List.of(queteNain);

        Aventurier pretresse = new Aventurier();
        pretresse.id = 4L;
        pretresse.nom = "Ysolde";
        pretresse.classe = "Prêtresse";
        pretresse.niveau = 7;
        pretresse.soldeOr = 95;

        Quete quetePretresse = new Quete();
        quetePretresse.id = 103L;
        quetePretresse.titre = "Bénir le puits empoisonné";
        quetePretresse.difficulte = "Facile";
        quetePretresse.recompenseOr = 60;

        pretresse.quetes = List.of(quetePretresse);

        Aventurier rodeur = new Aventurier();
        rodeur.id = 5L;
        rodeur.nom = "Thane";
        rodeur.classe = "Rôdeur";
        rodeur.niveau = 11;
        rodeur.soldeOr = 275;

        Quete queteRodeur = new Quete();
        queteRodeur.id = 104L;
        queteRodeur.titre = "Traquer le loup-garou de la forêt noire";
        queteRodeur.difficulte = "Difficile";
        queteRodeur.recompenseOr = 500;

        rodeur.quetes = List.of(queteRodeur);

        Aventurier barde = new Aventurier();
        barde.id = 6L;
        barde.nom = "Odran";
        barde.classe = "Barde";
        barde.niveau = 6;
        barde.soldeOr = 40;
        barde.quetes = List.of();

        Aventurier paladin = new Aventurier();
        paladin.id = 7L;
        paladin.nom = "Freya";
        paladin.classe = "Paladin";
        paladin.niveau = 18;
        paladin.soldeOr = 890;

        Quete quetePaladin = new Quete();
        quetePaladin.id = 105L;
        quetePaladin.titre = "Purifier le sanctuaire profané";
        quetePaladin.difficulte = "Difficile";
        quetePaladin.recompenseOr = 700;

        paladin.quetes = List.of(quetePaladin);

        Aventurier mage = new Aventurier();
        mage.id = 8L;
        mage.nom = "Corwin";
        mage.classe = "Mage";
        mage.niveau = 9;
        mage.soldeOr = 150;

        Quete queteMage = new Quete();
        queteMage.id = 106L;
        queteMage.titre = "Stabiliser le portail instable";
        queteMage.difficulte = "Moyenne";
        queteMage.recompenseOr = 220;

        mage.quetes = List.of(queteMage);

        aventuriers.add(guerrier);
        aventuriers.add(archere);
        aventuriers.add(nain);
        aventuriers.add(pretresse);
        aventuriers.add(rodeur);
        aventuriers.add(barde);
        aventuriers.add(paladin);
        aventuriers.add(mage);
    }

    /**
     * Recherche un aventurier par son identifiant unique.
     *
     * @param id l'identifiant de l'aventurier à rechercher.
     * @return l'aventurier correspondant, ou null s'il n'est pas trouvé.
     * @throws NullPointerException si l'id est null.
     */
    public Aventurier getAventurier(Long id) {
        Objects.requireNonNull(id, "L'identifiant ne peut pas être null");
        return aventuriers.stream()
                .filter(a -> a.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Ajoute un nouvel aventurier dans la taverne après validation des données.
     *
     * @param nom    le nom de l'aventurier (ne doit pas être vide).
     * @param classe la classe de l'aventurier (ne doit pas être vide).
     * @param niveau le niveau de l'aventurier (doit être supérieur à 0).
     * @return l'aventurier créé et enregistré.
     * @throws IllegalArgumentException si les données fournies sont invalides.
     */
    public Aventurier ajouterAventurier(String nom, String classe, Integer niveau) {
        validateAventurier(nom, classe, niveau);

        Aventurier aventurier = new Aventurier();
        aventurier.id = (long) (aventuriers.size() + 1);
        aventurier.nom = nom;
        aventurier.classe = classe;
        aventurier.niveau = niveau;
        aventurier.soldeOr = 0;
        aventurier.quetes = new ArrayList<>();

        aventuriers.add(aventurier);

        return aventurier;
    }

    private void validateAventurier(String nom, String classe, Integer niveau) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'aventurier est obligatoire");
        }
        if (classe == null || classe.isBlank()) {
            throw new IllegalArgumentException("La classe de l'aventurier est obligatoire");
        }
        if (niveau == null || niveau <= 0) {
            throw new IllegalArgumentException("Le niveau doit être un entier positif");
        }
    }
}