package fr.eletutour.tavern.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AventurierTest {

    @Test
    @DisplayName("Les getters/setters de Aventurier doivent fonctionner correctement")
    void testGettersSetters() {
        Aventurier aventurier = new Aventurier();
        aventurier.setId(1L);
        aventurier.setNom("Baldric");
        aventurier.setClasse("Guerrier");
        aventurier.setNiveau(5);

        Quete quete = new Quete();
        quete.setTitre("Tuer le dragon");
        quete.setRecompenseOr(1000);
        aventurier.setQuetes(List.of(quete));

        assertEquals(1L, aventurier.getId());
        assertEquals("Baldric", aventurier.getNom());
        assertEquals("Guerrier", aventurier.getClasse());
        assertEquals(5, aventurier.getNiveau());
        assertNotNull(aventurier.getQuetes());
        assertEquals(1, aventurier.getQuetes().size());
        assertEquals("Tuer le dragon", aventurier.getQuetes().getFirst().getTitre());
        assertEquals(1000, aventurier.getQuetes().getFirst().getRecompenseOr());
    }
}
