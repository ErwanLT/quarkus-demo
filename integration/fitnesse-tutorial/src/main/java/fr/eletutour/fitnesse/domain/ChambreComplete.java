package fr.eletutour.fitnesse.domain;

public class ChambreComplete extends RuntimeException {

    public ChambreComplete(Chambre chambre) {
        super(chambre.libelle() + " est complète");
    }
}
