package fr.eletutour.tavern.batch.batch;

import fr.eletutour.tavern.batch.service.TavernBreweryService;
import jakarta.batch.api.AbstractBatchlet;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.logging.Logger;

@Named("nightlyBrewBatchlet")
public class NightlyBrewBatchlet extends AbstractBatchlet {

    private static final Logger LOG = Logger.getLogger(NightlyBrewBatchlet.class);

    @Inject
    TavernBreweryService tavernBreweryService;

    @Override
    public String process() {
        LOG.info("Nightly brew batchlet started");
        int brewed = tavernBreweryService.brewConsumedBeers();
        LOG.infov("Nightly brew batchlet finished with brewed={0}", brewed);
        return "BREWED_" + brewed;
    }
}
