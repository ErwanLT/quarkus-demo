package fr.eletutour.exception.mapper;

import fr.eletutour.exception.business.BusinessException;
import fr.eletutour.exception.business.TavernError;
import fr.eletutour.exception.business.TavernException;
import fr.eletutour.exception.model.Problem;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class BusinessExceptionMapper extends AbstractExceptionMapper<BusinessException> {

    @Override
    protected int status(BusinessException ex) {
        return ex.getStatus();
    }

    @Override
    protected String title(BusinessException ex) {
        return ex.getTitle();
    }

    @Override
    protected String type(BusinessException ex) {
        return switch (ex) {
            case TavernException tavernException ->
                    "urn:problem:tavern:" + tavernException.getError().getCode();
            default -> "about:blank";
        };
    }

    @Override
    protected void enrich(Problem problem, BusinessException ex) {
        switch (ex) {
            case TavernException tavernException
                    when tavernException.getError() == TavernError.CAPACITY_REACHED -> {
                Map<String, Object> additional = baseAdditional(tavernException);
                int current = tavernException.getCurrentCapacity();
                int max = tavernException.getMaxCapacity();
                additional.put("currentCapacity", current);
                additional.put("maxCapacity", max);
                additional.put("availableSlots", max - current);
                problem.additional(additional);
            }
            case TavernException tavernException -> problem.additional(baseAdditional(tavernException));
            default -> {
            }
        }
    }

    private Map<String, Object> baseAdditional(TavernException tavernException) {
        Map<String, Object> additional = new HashMap<>();
        additional.put("code", tavernException.getError().getCode());
        return additional;
    }
}
