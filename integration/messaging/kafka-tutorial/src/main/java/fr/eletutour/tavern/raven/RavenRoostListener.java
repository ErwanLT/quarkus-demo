package fr.eletutour.tavern.raven;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Colombier de la taverne : accueille les corbeaux qui se posent depuis le
 * topic Kafka {@code raven-roost} et en extrait le message.
 * <p>
 * Chaque message recu declenche {@link #onRavenLanding(RavenMessage)}, qui
 * joue ici le role du garcon d'ecurie detachant le parchemin de la patte du
 * corbeau avant de le lire.
 */
@ApplicationScoped
public class RavenRoostListener {

    private static final Logger LOG = Logger.getLogger(RavenRoostListener.class);

    /**
     * Traite un corbeau qui vient de se poser au colombier.
     *
     * @param message message porte par le corbeau
     */
    @Incoming("raven-inbound")
    @Retry(maxRetries = 3, delay = 1000)
    public void onRavenLanding(RavenMessage message) {
        LOG.infof("Un corbeau se pose : message de %s -> \"%s\" (envoye a %s)",
                message.sender(), message.content(), message.sentAt());

        if (message.content().contains("fail") || message.content().contains("erreur")) {
            LOG.warnf("Le message contient un mot déclenchant une erreur ! Tentative de traitement en échec pour : %s", message.content());
            throw new RuntimeException("Echec simule du traitement pour le message: " + message.content());
        }
    }
}
