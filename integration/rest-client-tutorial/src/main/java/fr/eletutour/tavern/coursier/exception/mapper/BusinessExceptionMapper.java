package fr.eletutour.tavern.coursier.exception.mapper;

import fr.eletutour.tavern.coursier.exception.business.BusinessException;
import fr.eletutour.tavern.coursier.exception.business.CoursierException;
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
            case CoursierException coursier -> "urn:problem:coursier:" + coursier.getError().getCode();
            default -> "about:blank";
        };
    }

    @Override
    protected Map<String, Object> additional(BusinessException ex) {
        return switch (ex) {
            case CoursierException coursier -> {
                Map<String, Object> additional = new HashMap<>(coursier.getDetails());
                additional.put("code", coursier.getError().getCode());
                yield additional;
            }
            default -> null;
        };
    }
}
