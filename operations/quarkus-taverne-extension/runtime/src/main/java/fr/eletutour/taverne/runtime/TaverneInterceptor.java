package fr.eletutour.taverne.runtime;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

/**
 * Le sortilège. Chaque méthode marquée {@code @Taverne} passe ici avant
 * d'être exécutée : le tavernier annonce la quête, laisse le héros agir,
 * puis commente le résultat.
 */
@Taverne
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class TaverneInterceptor {

    private static final Logger LOG = Logger.getLogger("la-taverne");

    @Inject
    TaverneConfig config;

    @AroundInvoke
    Object crierLaQuete(InvocationContext context) throws Exception {
        if (!config.enabled()) {
            return context.proceed();
        }

        String quete = resoudreNomQuete(context);
        annoncer(quete);

        try {
            Object resultat = context.proceed();
            LOG.infof("[la-taverne] %s : quête accomplie.", quete);
            return resultat;
        } catch (Exception e) {
            LOG.warnf("[la-taverne] %s : la quête a échoué (%s).", quete, e.getMessage());
            throw e;
        }
    }

    private String resoudreNomQuete(InvocationContext context) {
        Taverne annotation = context.getMethod().getAnnotation(Taverne.class);
        if (annotation == null) {
            annotation = context.getTarget().getClass().getAnnotation(Taverne.class);
        }
        if (annotation != null && !annotation.quete().isBlank()) {
            return annotation.quete();
        }
        return context.getMethod().getName();
    }

    private void annoncer(String quete) {
        switch (config.ton()) {
            case GROGNON -> LOG.infof("[la-taverne] Encore une quête... %s. Bon.", quete);
            case SOBRE -> LOG.infof("[la-taverne] Invocation : %s", quete);
            case BARDE -> LOG.infof("[la-taverne] Que l'on m'entende ! La quête « %s » commence !", quete);
        }
    }
}
