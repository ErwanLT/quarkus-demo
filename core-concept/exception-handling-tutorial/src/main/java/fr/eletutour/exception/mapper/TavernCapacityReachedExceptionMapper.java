package fr.eletutour.exception.mapper;

import fr.eletutour.exception.business.TavernCapacityReachedException;
import fr.eletutour.exception.model.Problem;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class TavernCapacityReachedExceptionMapper extends AbstractExceptionMapper<TavernCapacityReachedException> {

    @Override
    protected int status(TavernCapacityReachedException ex) {
        return ex.getStatus();
    }

    @Override
    protected String title(TavernCapacityReachedException ex) {
        return ex.getTitle();
    }

    @Override
    protected void enrich(Problem problem, TavernCapacityReachedException ex) {
        problem.additional(Map.of(
            "currentCapacity", ex.getCurrentCapacity(),
            "maxCapacity", ex.getMaxCapacity(),
            "availableSlots", ex.getMaxCapacity() - ex.getCurrentCapacity()
        ));
    }
}
