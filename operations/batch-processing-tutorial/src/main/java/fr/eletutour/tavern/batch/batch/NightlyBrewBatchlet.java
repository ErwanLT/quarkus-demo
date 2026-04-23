package fr.eletutour.tavern.batch.batch;

import fr.eletutour.tavern.batch.service.TavernBreweryService;
import jakarta.batch.api.AbstractBatchlet;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("nightlyBrewBatchlet")
public class NightlyBrewBatchlet extends AbstractBatchlet {

    @Inject
    TavernBreweryService tavernBreweryService;

    @Override
    public String process() {
        int brewed = tavernBreweryService.brewConsumedBeers();
        return "BREWED_" + brewed;
    }
}
