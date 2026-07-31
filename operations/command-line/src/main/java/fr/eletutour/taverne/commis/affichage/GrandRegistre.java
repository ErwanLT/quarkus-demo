package fr.eletutour.taverne.commis.affichage;

import java.util.ArrayList;
import java.util.List;

/**
 * Construit un tableau texte bordé, aux colonnes alignées automatiquement selon
 * le contenu le plus large de chacune. Remplace les {@code printf} alignés à la
 * main, fragiles dès qu'une colonne change de contenu.
 *
 * <p>Le rendu est séparé en trois morceaux (entête, lignes, pied) plutôt qu'une
 * seule chaîne, pour permettre à l'appelant de colorer certaines lignes
 * individuellement avant affichage (voir {@link SceauDuTavernier}).</p>
 */
public class GrandRegistre {

    private final List<String> entetes;
    private final List<List<String>> lignes = new ArrayList<>();

    public GrandRegistre(List<String> entetes) {
        this.entetes = entetes;
    }

    /**
     * Ajoute une ligne de données au registre.
     *
     * @param valeurs les valeurs de la ligne, dans l'ordre des entêtes
     * @throws IllegalArgumentException si le nombre de valeurs ne correspond pas au nombre d'entêtes
     */
    public void ajouterLigne(List<String> valeurs) {
        if (valeurs.size() != entetes.size()) {
            throw new IllegalArgumentException("Le nombre de valeurs ne correspond pas au nombre d'entetes.");
        }
        lignes.add(valeurs);
    }

    /**
     * @return la ligne de séparation, suivie de la ligne d'entête, suivie d'une nouvelle séparation
     */
    public String construireEntete() {
        int[] largeurs = calculerLargeurs();
        String separateur = construireSeparateur(largeurs);
        return separateur + System.lineSeparator()
                + construireLigne(entetes, largeurs) + System.lineSeparator()
                + separateur;
    }

    /**
     * @return une ligne formatée par entrée ajoutée via {@link #ajouterLigne}, dans l'ordre d'ajout
     */
    public List<String> construireLignes() {
        int[] largeurs = calculerLargeurs();
        List<String> resultat = new ArrayList<>();
        for (List<String> ligne : lignes) {
            resultat.add(construireLigne(ligne, largeurs));
        }
        return resultat;
    }

    /**
     * @return la ligne de séparation finale, de la même largeur que le reste du tableau
     */
    public String construirePied() {
        return construireSeparateur(calculerLargeurs());
    }

    private int[] calculerLargeurs() {
        int[] largeurs = new int[entetes.size()];
        for (int colonne = 0; colonne < entetes.size(); colonne++) {
            largeurs[colonne] = entetes.get(colonne).length();
        }
        for (List<String> ligne : lignes) {
            for (int colonne = 0; colonne < ligne.size(); colonne++) {
                largeurs[colonne] = Math.max(largeurs[colonne], ligne.get(colonne).length());
            }
        }
        return largeurs;
    }

    private String construireLigne(List<String> valeurs, int[] largeurs) {
        StringBuilder ligne = new StringBuilder("|");
        for (int colonne = 0; colonne < valeurs.size(); colonne++) {
            ligne.append(" ").append(alignerAGauche(valeurs.get(colonne), largeurs[colonne])).append(" |");
        }
        return ligne.toString();
    }

    private String construireSeparateur(int[] largeurs) {
        StringBuilder separateur = new StringBuilder("+");
        for (int largeur : largeurs) {
            separateur.append("-".repeat(largeur + 2)).append("+");
        }
        return separateur.toString();
    }

    private String alignerAGauche(String valeur, int largeur) {
        return valeur + " ".repeat(largeur - valeur.length());
    }
}
