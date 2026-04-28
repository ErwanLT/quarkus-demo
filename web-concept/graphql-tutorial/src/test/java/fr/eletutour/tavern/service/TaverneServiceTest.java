package fr.eletutour.tavern.service;

import fr.eletutour.tavern.model.Aventurier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaverneServiceTest {

    private TaverneService taverneService;

    @BeforeEach
    void setUp() {
        taverneService = new TaverneService();
    }

    @Test
    @DisplayName("Devrait retourner les données initiales quand la liste est vide")
    void testGetAllAventuriersInitialData() {
        List<Aventurier> result = taverneService.getAllAventuriers();
        
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Baldric", result.getFirst().nom);
    }

    @Test
    @DisplayName("Devrait ajouter un aventurier valide")
    void testAjouterAventurierValide() {
        Aventurier nouveau = taverneService.ajouterAventurier("Elara", "Rôdeuse", 5);
        
        assertNotNull(nouveau);
        assertEquals("Elara", nouveau.nom);
        assertEquals("Rôdeuse", nouveau.classe);
        assertEquals(5, nouveau.niveau);
    }

    @Test
    @DisplayName("Devrait lancer une exception si le nom est vide lors de l'ajout")
    void testAjouterAventurierNomInvalide() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            taverneService.ajouterAventurier("", "Guerrier", 10)
        );
        assertEquals("Le nom de l'aventurier est obligatoire", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lancer une exception si le niveau est négatif lors de l'ajout")
    void testAjouterAventurierNiveauInvalide() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            taverneService.ajouterAventurier("Kael", "Mage", -1)
        );
        assertEquals("Le niveau doit être un entier positif", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait trouver un aventurier par son ID")
    void testGetAventurierExistant() {
        // Déclenche l'initData
        taverneService.getAllAventuriers();
        
        Aventurier result = taverneService.getAventurier(1L);
        
        assertNotNull(result);
        assertEquals("Baldric", result.nom);
    }

    @Test
    @DisplayName("Devrait retourner null si l'aventurier n'existe pas")
    void testGetAventurierInexistant() {
        Aventurier result = taverneService.getAventurier(999L);
        assertNull(result);
    }

    @Test
    @DisplayName("Devrait lancer une NullPointerException si l'ID est null")
    void testGetAventurierIdNull() {
        assertThrows(NullPointerException.class, () -> taverneService.getAventurier(null));
    }
}
