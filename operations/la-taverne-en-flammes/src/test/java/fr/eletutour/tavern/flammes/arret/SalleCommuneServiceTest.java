package fr.eletutour.tavern.flammes.arret;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("La salle commune suit les tournees encore en cours")
class SalleCommuneServiceTest {

    @Test
    @DisplayName("sert la tournee et libere le compteur a la fin du service")
    void doitLibererLeCompteurALaFinDuService() {
        SalleCommuneService salleCommuneService = new SalleCommuneService();

        TourneeServie tournee = salleCommuneService.servirTournee("Grimgor", 0);

        assertEquals("Grimgor", tournee.aventurier());
        assertEquals(0L, tournee.dureeServiceMs());
        assertEquals(0, tournee.tourneesEncoreEnCours());
        assertEquals(0, salleCommuneService.tourneesEnCours());
    }

    @Test
    @DisplayName("nomme l'aventurier anonyme quand aucun nom n'est donne")
    void doitNommerLAventurierAnonyme() {
        SalleCommuneService salleCommuneService = new SalleCommuneService();

        TourneeServie tournee = salleCommuneService.servirTournee(null, 0);

        assertEquals("aventurier anonyme", tournee.aventurier());
    }

    @Test
    @DisplayName("ramene une duree negative a zero seconde")
    void doitRamenerUneDureeNegativeAZero() {
        SalleCommuneService salleCommuneService = new SalleCommuneService();

        TourneeServie tournee = salleCommuneService.servirTournee("Grimgor", -5);

        assertEquals(0L, tournee.dureeServiceMs());
    }
}
