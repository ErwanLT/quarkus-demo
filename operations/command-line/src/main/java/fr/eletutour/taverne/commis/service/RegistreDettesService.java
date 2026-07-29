package fr.eletutour.taverne.commis.service;

import fr.eletutour.taverne.commis.domain.DetteAventurier;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tient le registre des dettes des aventuriers de la taverne.
 *
 * <p>Dans un projet reel, ce service s'appuierait probablement sur un repository
 * (base de donnees, fichier, API externe). Ici, il joue le role du grand livre
 * de comptes que le tavernier confie au commis avant sa course.</p>
 */
@ApplicationScoped
public class RegistreDettesService {

    /**
     * Retourne l'ensemble des dettes actuellement enregistrees.
     *
     * @return la liste des dettes, jamais {@code null}
     */
    public List<DetteAventurier> listerDettes() {
        return List.of(
                new DetteAventurier("Aragorn", new BigDecimal("42.50"), 12),
                new DetteAventurier("Boromir", new BigDecimal("128.00"), 45),
                new DetteAventurier("Gimli", new BigDecimal("7.30"), 90)
        );
    }
}
