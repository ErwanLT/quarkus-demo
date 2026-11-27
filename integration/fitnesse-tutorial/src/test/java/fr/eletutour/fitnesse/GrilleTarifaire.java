package fr.eletutour.fitnesse;

import fr.eletutour.fitnesse.domain.Chambre;
import fr.eletutour.fitnesse.domain.Devis;
import fr.eletutour.fitnesse.domain.Tarif;

/**
 * Table de décision branchée directement sur le code métier : pas de HTTP,
 * pas de Quarkus, juste la classe {@link Tarif}. C'est le cas le plus rapide
 * et le plus courant pour des règles de calcul.
 */
public class GrilleTarifaire {

    private final Tarif tarif = new Tarif();

    private String chambre;
    private int nuits;
    private boolean membre;
    private Devis devis;

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public void setNuits(int nuits) {
        this.nuits = nuits;
    }

    public void setMembreDeLaGuilde(String membre) {
        this.membre = ClientTaverne.ouiNon(membre);
    }

    /** Appelé par Slim après les setters de chaque ligne. */
    public void execute() {
        devis = tarif.devis(Chambre.depuis(chambre), nuits, membre);
    }

    /** Remis à zéro entre deux lignes. */
    public void reset() {
        devis = null;
    }

    public int prixDeBase() {
        return devis.prixDeBase();
    }

    public String remises() {
        return devis.remisesLisibles();
    }

    public int aPayer() {
        return devis.total();
    }
}
