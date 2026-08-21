package fr.eletutour.tavern.flammes.sante;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Le tocsin de la taverne : porte l'etat de l'incident et tient la chronologie des evenements.
 *
 * <p>C'est la seule source de verite consultee par les sondes de sante, le runbook et le
 * post-mortem. Le declenchement se fait a la main via l'API pour pouvoir rejouer le scenario.</p>
 */
@ApplicationScoped
public class IncendieService {

    private static final Logger LOG = Logger.getLogger(IncendieService.class);
    private static final String ORIGINE_INCONNUE = "aucune";

    private final AtomicBoolean cuisineEnFeu = new AtomicBoolean(false);
    private final AtomicBoolean charpenteRompue = new AtomicBoolean(false);
    private final AtomicReference<String> origineIncendie = new AtomicReference<>(ORIGINE_INCONNUE);
    private final List<EvenementIncendie> chronologie = new CopyOnWriteArrayList<>();

    public EtatTaverne etat() {
        return new EtatTaverne(cuisineEnFeu.get(), charpenteRompue.get(), origineIncendie.get());
    }

    public List<EvenementIncendie> chronologie() {
        return List.copyOf(chronologie);
    }

    public EtatTaverne declencherIncendie(String origine) {
        String origineRetenue = (origine == null || origine.isBlank()) ? "friture de la cuisine" : origine;
        if (cuisineEnFeu.compareAndSet(false, true)) {
            origineIncendie.set(origineRetenue);
            noter("DEBUT_INCENDIE", "Le feu prend : " + origineRetenue);
            noter("TOCSIN", "La cloche sonne, la readiness passe DOWN, l'orchestrateur retire l'instance du routage");
            LOG.warnf("Incendie declenche dans la taverne : origine=%s", origineRetenue);
        }
        return etat();
    }

    public EtatTaverne eteindreIncendie() {
        if (cuisineEnFeu.compareAndSet(true, false)) {
            noter("EXTINCTION", "Les flammes sont maitrisees, la readiness peut repasser UP");
            LOG.infof("Incendie eteint : origine traitee=%s", origineIncendie.get());
        }
        return etat();
    }

    public EtatTaverne effondrerCharpente() {
        if (charpenteRompue.compareAndSet(false, true)) {
            noter("EFFONDREMENT", "La charpente cede : liveness DOWN, l'instance doit etre remplacee");
            LOG.error("Charpente effondree : la taverne ne tient plus debout");
        }
        return etat();
    }

    public EtatTaverne releverCharpente() {
        if (charpenteRompue.compareAndSet(true, false)) {
            noter("RECONSTRUCTION", "La charpente est etayee, la taverne tient a nouveau debout");
            LOG.info("Charpente relevee");
        }
        return etat();
    }

    public EtatTaverne reinitialiser() {
        cuisineEnFeu.set(false);
        charpenteRompue.set(false);
        origineIncendie.set(ORIGINE_INCONNUE);
        chronologie.clear();
        LOG.debug("Chronologie de l'incident remise a zero");
        return etat();
    }

    public void noter(String etape, String detail) {
        chronologie.add(new EvenementIncendie(Instant.now(), etape, detail));
    }
}
