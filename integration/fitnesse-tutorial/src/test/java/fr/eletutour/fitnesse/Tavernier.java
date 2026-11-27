package fr.eletutour.fitnesse;

/**
 * Fixture d'intendance, utilisée par SuiteSetUp et SetUp : vérifier que la
 * taverne est ouverte, puis effacer le registre avant chaque page pour que
 * les pages restent indépendantes les unes des autres.
 */
public class Tavernier {

    public boolean laTaverneEstOuverte() {
        return ClientTaverne.get("/taverne/accueil").statusCode() == 200;
    }

    public boolean remettreLeRegistreAZero() {
        return ClientTaverne.delete("/taverne/reservations").statusCode() == 204;
    }
}
