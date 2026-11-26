package fr.eletutour.validation.mapper;

import fr.eletutour.validation.business.BusinessException;
import fr.eletutour.validation.business.TavernException;
import fr.eletutour.validation.model.Problem;
import jakarta.ws.rs.ext.Provider;

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
        if (ex instanceof TavernException tavernException) {
            return "urn:problem:tavern:" + tavernException.getError().getCode();
        }
        return "about:blank";
    }

    @Override
    protected void enrich(Problem problem, BusinessException ex) {
        if (ex instanceof TavernException tavernException) {
            problem.additional(Map.of("code", tavernException.getError().getCode()));
        }
    }
}
