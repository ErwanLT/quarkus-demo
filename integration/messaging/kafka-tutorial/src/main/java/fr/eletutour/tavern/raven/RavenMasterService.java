package fr.eletutour.tavern.raven;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Maitre corbeau de la taverne : responsable de lacher les messages sur le
 * topic Kafka {@code raven-roost}.
 * <p>
 * Chaque appel a {@link #dispatch(String, String)} equivaut a attacher un
 * message a la patte d'un corbeau et a l'envoyer prendre son envol. Le
 * service ne garantit pas quand le message sera lu, seulement qu'il a bien
 * quitte la taverne.
 */
@ApplicationScoped
public class RavenMasterService {

    private static final Logger LOG = Logger.getLogger(RavenMasterService.class);

    @Inject
    @Channel("raven-outbound")
    Emitter<RavenMessage> ravenOutbound;

    /**
     * Confie un nouveau message a un corbeau et le lache vers le topic Kafka.
     *
     * @param sender  expediteur du message
     * @param content contenu du message
     * @return le message tel qu'il a ete confie au corbeau
     */
    public RavenMessage dispatch(String sender, String content) {
        RavenMessage message = RavenMessage.from(sender, content);
        LOG.infof("Un corbeau quitte la taverne, envoye par %s", sender);
        ravenOutbound.send(message);
        return message;
    }
}
