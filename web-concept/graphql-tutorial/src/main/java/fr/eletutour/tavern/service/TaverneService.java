package fr.eletutour.tavern.service;

import fr.eletutour.tavern.model.Aventurier;
import fr.eletutour.tavern.model.Quete;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service gérant la logique métier de la taverne et de ses aventuriers.
 */
@ApplicationScoped
public class TaverneService {

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

    private void initData() {
        Aventurier guerrier = new Aventurier();
        guerrier.id = 1L;
        guerrier.nom = "Baldric";
        guerrier.classe = "Guerrier";
        guerrier.niveau = 12;

        Quete quete = new Quete();
        quete.id = 100L;
        quete.titre = "Nettoyer les caves infestées";
        quete.difficulte = "Moyenne";
        quete.recompenseOr = 250;

        guerrier.quetes = List.of(quete);

        aventuriers.add(guerrier);
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
