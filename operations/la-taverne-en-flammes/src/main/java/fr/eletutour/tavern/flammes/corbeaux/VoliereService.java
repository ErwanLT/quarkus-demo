package fr.eletutour.tavern.flammes.corbeaux;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * La voliere : elle lache des corbeaux dans le canal {@code corbeaux-en-vol}.
 *
 * <p>Le canal est ici in-VM pour rester executable sans broker, mais la mecanique de vidange est
 * la meme qu'avec le connecteur Kafka de SmallRye Reactive Messaging : a l'arret, la reception
 * est coupee, le pipeline en cours se termine, puis les ressources sont liberees.</p>
 */
@ApplicationScoped
public class VoliereService {

    private static final Logger LOG = Logger.getLogger(VoliereService.class);
    private static final int NOMBRE_MAX_PAR_LACHER = 500;

    private final AtomicLong corbeauxLaches = new AtomicLong();
    private final AtomicLong corbeauxTraites = new AtomicLong();

    @Inject
    @Channel("corbeaux-en-vol")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 2048)
    Emitter<Corbeau> emetteur;

    public EtatVoliere etat() {
        long laches = corbeauxLaches.get();
        long traites = corbeauxTraites.get();
        return new EtatVoliere(laches, traites, Math.max(0L, laches - traites));
    }

    public EtatVoliere lacher(int nombre) {
        int aLacher = Math.clamp(nombre, 1, NOMBRE_MAX_PAR_LACHER);
        for (int index = 0; index < aLacher; index++) {
            long numero = corbeauxLaches.incrementAndGet();
            emetteur.send(new Corbeau(numero, "Commande a livrer au comptoir n." + numero));
        }
        LOG.infof("Corbeaux laches : nombre=%d, enVol=%d", aLacher, etat().corbeauxEnVol());
        return etat();
    }

    void marquerTraite(Corbeau corbeau) {
        corbeauxTraites.incrementAndGet();
        LOG.debugf("Pli lu : numero=%d", corbeau.numero());
    }

    /**
     * Vidange : attend que les plis deja recuperes soient lus, dans la limite du delai accorde.
     *
     * <p>Avec le connecteur Kafka de SmallRye, cette attente est prise en charge par le
     * connecteur lui-meme (fermeture du consumer, fin du lot en cours, commit des offsets) et les
     * enregistrements non encore recuperes restent sur le topic. Sur un canal in-VM il n'y a ni
     * broker ni offset : c'est a l'application de retenir l'arret, sinon le retard en memoire est
     * simplement perdu.</p>
     *
     * @return les corbeaux encore en vol a la fin de l'attente
     */
    public long attendreVidange(Duration delaiMaximum) {
        Instant limite = Instant.now().plus(delaiMaximum);
        long enVol = etat().corbeauxEnVol();

        while (enVol > 0 && Instant.now().isBefore(limite)) {
            patienter();
            enVol = etat().corbeauxEnVol();
        }

        if (enVol > 0) {
            LOG.warnf("Vidange incomplete : %d plis perdus faute de temps", enVol);
        } else {
            LOG.info("Vidange terminee : tous les plis recuperes ont ete lus");
        }
        return enVol;
    }

    private void patienter() {
        try {
            Thread.sleep(25L);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Vidange de la voliere interrompue", interruptedException);
        }
    }

    public EtatVoliere reinitialiser() {
        corbeauxLaches.set(0L);
        corbeauxTraites.set(0L);
        return etat();
    }
}
