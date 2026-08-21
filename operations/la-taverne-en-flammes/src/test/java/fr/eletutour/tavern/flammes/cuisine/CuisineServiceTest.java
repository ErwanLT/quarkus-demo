package fr.eletutour.tavern.flammes.cuisine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("La cuisine reactive borne l'attente d'un plat")
class CuisineServiceTest {

    private CuisineService cuisineService;

    @BeforeEach
    void preparerLaCuisine() {
        cuisineService = new CuisineService();
        cuisineService.timeoutCuisine = Duration.ofMillis(200);
    }

    @Test
    @DisplayName("sert le plat demande quand la cuisine tient le delai")
    void doitServirLePlatQuandLaCuisineTientLeDelai() {
        Repas repas = cuisineService.commanderRepas("ragout de sanglier", 20L).await().indefinitely();

        assertEquals("ragout de sanglier", repas.plat());
        assertEquals(Repas.STATUT_SERVI, repas.statut());
        assertEquals(1L, cuisineService.platsServis());
        assertEquals(0L, cuisineService.repasDeSecours());
    }

    @Test
    @DisplayName("bascule sur pain et eau quand le delai est depasse")
    void doitBasculerSurLeRepasDeSecoursQuandLeDelaiEstDepasse() {
        Repas repas = cuisineService.commanderRepas("cuissot de dragon", 800L).await().indefinitely();

        assertEquals("Pain et Eau", repas.plat());
        assertEquals(Repas.STATUT_SECOURS, repas.statut());
        assertEquals(0L, cuisineService.platsServis());
        assertEquals(1L, cuisineService.repasDeSecours());
    }

    @Test
    @DisplayName("retient un plat par defaut quand l'aventurier ne sait pas quoi commander")
    void doitRetenirUnPlatParDefautQuandAucunPlatNEstDemande() {
        Repas repas = cuisineService.commanderRepas("  ", 20L).await().indefinitely();

        assertEquals("ragout de sanglier", repas.plat());
        assertEquals(Repas.STATUT_SERVI, repas.statut());
    }

    @Test
    @DisplayName("remet les compteurs a zero pour rejouer le scenario")
    void doitRemettreLesCompteursAZero() {
        cuisineService.commanderRepas("tourte", 20L).await().indefinitely();
        cuisineService.reinitialiserCompteurs();

        assertEquals(0L, cuisineService.platsServis());
        assertEquals(0L, cuisineService.repasDeSecours());
    }
}
