package fr.eletutour.tavern.flammes.comptes;

import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Le coffre-fort ignifuge : tout ce qui touche au grand livre passe par une transaction JTA.
 *
 * <p>Trois scenarios sont exposes : l'ecriture nominale, l'ecriture qui echoue en cours de route
 * (pour verifier qu'on ne laisse pas une tournee a moitie facturee), et l'ecriture trop lente
 * (pour verifier que la base ralentie par l'incident ne garde pas ses verrous indefiniment).</p>
 */
@ApplicationScoped
public class GrandLivreService {

    private static final Logger LOG = Logger.getLogger(GrandLivreService.class);

    /**
     * Atomicite : les deux lignes de la tournee sont ecrites, ou aucune ne l'est.
     */
    @Transactional
    public List<Ecriture> enregistrerTournee(String aventurier, int montantPiecesOr) {
        LigneDeCompte consommation = new LigneDeCompte(aventurier, "Tournee de biere", montantPiecesOr);
        consommation.persist();
        LigneDeCompte taxe = new LigneDeCompte(aventurier, "Taxe de la guilde", Math.max(1, montantPiecesOr / 10));
        taxe.persist();

        LOG.infof("Tournee facturee : aventurier=%s, montant=%d", aventurier, montantPiecesOr);
        return List.of(Ecriture.de(consommation), Ecriture.de(taxe));
    }

    /**
     * L'incendie interrompt le tavernier entre les deux ecritures : JTA annule tout.
     */
    @Transactional
    public void enregistrerTourneeInterrompue(String aventurier, int montantPiecesOr) {
        new LigneDeCompte(aventurier, "Tournee de biere", montantPiecesOr).persist();
        LOG.warnf("Ecriture partielle en base, une poutre tombe sur le comptoir : aventurier=%s", aventurier);
        throw new IllegalStateException("Une poutre en flammes emporte le grand livre avant la fin de l'ecriture");
    }

    /**
     * La base est ralentie par l'incident : au-dela du timeout, Narayana annule la transaction
     * plutot que de laisser les verrous SQL bloques.
     */
    @Transactional
    @TransactionConfiguration(timeout = 2)
    public void enregistrerTourneeLente(String aventurier, int montantPiecesOr, long attenteMs) {
        new LigneDeCompte(aventurier, "Tournee de biere (base ralentie)", montantPiecesOr).persist();
        attendre(attenteMs);
        // La transaction peut deja avoir ete annulee par le reaper Narayana : le verdict
        // tombe au commit, pas ici.
        LOG.debugf("Fin du corps de la transaction lente, commit remis a JTA : aventurier=%s, attente=%sms",
            aventurier, attenteMs);
    }

    public List<Ecriture> grandLivre() {
        return LigneDeCompte.<LigneDeCompte>listAll().stream()
            .map(Ecriture::de)
            .toList();
    }

    public long nombreEcritures() {
        return LigneDeCompte.count();
    }

    @Transactional
    public long purger() {
        long supprimees = LigneDeCompte.deleteAll();
        LOG.debugf("Grand livre purge : %d lignes supprimees", supprimees);
        return supprimees;
    }

    private void attendre(long attenteMs) {
        try {
            Thread.sleep(Math.max(0L, attenteMs));
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ecriture interrompue", interruptedException);
        }
    }
}
